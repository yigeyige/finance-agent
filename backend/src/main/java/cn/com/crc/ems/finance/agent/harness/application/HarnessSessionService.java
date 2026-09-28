package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.harness.domain.PlanRevision;
import cn.com.crc.ems.finance.agent.harness.domain.SessionState;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/** 管理 Harness session 与计划修订的业务绑定，计划改写不自动推进任务状态。 */
@Service
public class HarnessSessionService {

    private final SessionStateRepository sessions;
    private final PlanRevisionRepository plans;
    private final Clock clock;

    public HarnessSessionService(SessionStateRepository sessions, PlanRevisionRepository plans, Clock clock) {
        this.sessions = Objects.requireNonNull(sessions, "sessions is required");
        this.plans = Objects.requireNonNull(plans, "plans is required");
        this.clock = Objects.requireNonNull(clock, "clock is required");
    }

    /** 创建带任务、用户、运行版本和范围绑定的 session。 */
    @Transactional
    public SessionState start(HarnessSessionBinding binding, String harnessVersion,
            StateReference stateRef, UUID actorUserId, Instant occurredAt) {
        if (binding == null || harnessVersion == null || harnessVersion.isBlank()
                || stateRef == null || actorUserId == null || occurredAt == null) {
            throw new IllegalArgumentException("session binding, version, state and actor are required");
        }
        if (sessions.findById(binding.sessionId(), binding.taskId(), binding.userId()).isPresent()) {
            throw new IllegalStateException("session already exists for task and user");
        }
        SessionState state = new SessionState(binding.sessionId(), binding.taskId(), binding.userId(), harnessVersion,
                stateRef, actorUserId, occurredAt, actorUserId, occurredAt);
        sessions.insert(state);
        return state;
    }

    /** 追加只读 Harness 计划修订；不会创建步骤、审批卡或工具调用。 */
    @Transactional
    public PlanRevision appendPlan(HarnessSessionBinding binding, StateReference planRef,
            UUID actorUserId, Instant occurredAt) {
        requireBoundSession(binding);
        if (planRef == null || actorUserId == null || occurredAt == null) {
            throw new IllegalArgumentException("planRef, actorUserId and occurredAt are required");
        }
        List<PlanRevision> existing = plans.findBySessionId(binding.sessionId(), binding.taskId(), binding.userId());
        int revisionNo = existing.stream().mapToInt(PlanRevision::revisionNo).max().orElse(0) + 1;
        PlanRevision revision = new PlanRevision(UuidGenerator.newUuid(), binding.sessionId(), binding.taskId(),
                binding.userId(), revisionNo, planRef, actorUserId, occurredAt, actorUserId, occurredAt);
        plans.insert(revision);
        return revision;
    }

    /** 将计划映射为后续业务审批输入，调用方必须显式提交该命令。 */
    public cn.com.crc.ems.finance.agent.task.application.ProposeStepsCommand mapPlan(
            HarnessSessionBinding binding, PlanRevision plan, UUID actorUserId) {
        requireBoundSession(binding);
        return HarnessPlanMapper.toStepProposal(plan, binding, actorUserId, Instant.now(clock));
    }

    private void requireBoundSession(HarnessSessionBinding binding) {
        if (binding == null) {
            throw new IllegalArgumentException("binding is required");
        }
        sessions.findById(binding.sessionId(), binding.taskId(), binding.userId())
                .orElseThrow(() -> new IllegalStateException("session is not bound to task and user"));
    }
}
