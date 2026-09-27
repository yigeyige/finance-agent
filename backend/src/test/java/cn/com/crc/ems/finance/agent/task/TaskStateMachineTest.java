package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.domain.InvalidTaskTransitionException;
import cn.com.crc.ems.finance.agent.task.domain.TaskStateMachine;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 验证附件 A 任务状态转移矩阵和暂停/取消守卫。 */
class TaskStateMachineTest {

    private final TaskStateMachine machine = new TaskStateMachine();

    @Test
    void normalPathFollowsAttachmentStateTable() {
        assertThat(machine.transition(TaskStatus.DRAFT, TaskStatus.WAITING_SCOPE_CONFIRMATION, "submit", false)
                .toStatus()).isEqualTo(TaskStatus.WAITING_SCOPE_CONFIRMATION);
        assertThat(machine.transition(TaskStatus.RUNNING_STEP, TaskStatus.ANALYZING, "tool-complete", false)
                .toStatus()).isEqualTo(TaskStatus.ANALYZING);
        assertThat(machine.transition(TaskStatus.RECHECKING_SCOPE, TaskStatus.READY_TO_PUBLISH, "recheck-clean", false)
                .toStatus()).isEqualTo(TaskStatus.READY_TO_PUBLISH);
    }

    @Test
    void pauseResumeRequiresExplicitApprovalGuard() {
        assertThat(machine.transition(TaskStatus.PAUSED_MODEL_UNAVAILABLE, TaskStatus.WAITING_STEP_APPROVAL,
                "model-recovered", false).toStatus()).isEqualTo(TaskStatus.WAITING_STEP_APPROVAL);
        assertThatThrownBy(() -> machine.transition(TaskStatus.PAUSED_MODEL_UNAVAILABLE, TaskStatus.RUNNING_STEP,
                "resume", false)).isInstanceOf(InvalidTaskTransitionException.class);
        assertThat(machine.transition(TaskStatus.PAUSED_MODEL_UNAVAILABLE, TaskStatus.RUNNING_STEP,
                "resume-approved", true).toStatus()).isEqualTo(TaskStatus.RUNNING_STEP);
    }

    @Test
    void cancellationAndInvalidTransitionsAreGuarded() {
        assertThat(machine.transition(TaskStatus.WAITING_REVIEW, TaskStatus.CANCELLED, "operator-cancel", false)
                .toStatus()).isEqualTo(TaskStatus.CANCELLED);
        assertThatThrownBy(() -> machine.transition(TaskStatus.READY, TaskStatus.PUBLISHED, "skip", false))
                .isInstanceOf(InvalidTaskTransitionException.class);
        assertThatThrownBy(() -> machine.transition(TaskStatus.CLOSED, TaskStatus.CANCELLED, "close", false))
                .isInstanceOf(InvalidTaskTransitionException.class);
        assertThatThrownBy(() -> machine.transition(TaskStatus.READY, TaskStatus.CANCELLED, "", false))
                .isInstanceOf(InvalidTaskTransitionException.class);
    }

    @Test
    void transitionTableIsCompleteForStaleAndTerminalPaths() {
        assertThat(machine.transitionTable().get(TaskStatus.STALE_APPROVAL))
                .containsExactly(TaskStatus.WAITING_STEP_APPROVAL);
        assertThat(machine.transitionTable().get(TaskStatus.HANDED_OFF)).containsExactly(TaskStatus.CLOSED);
        assertThat(machine.transitionTable().get(TaskStatus.CLOSED)).isNull();
    }
}
