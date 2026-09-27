package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.domain.ToolInvocationKey;

class ToolInvocationKeyTest {

    @Test
    void formatsAllPartsDeterministically() {
        UUID taskId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID stepId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        assertThat(new ToolInvocationKey(taskId, stepId, 3, 4).value())
                .isEqualTo(taskId + ":" + stepId + ":3:4");
    }

    @Test
    void rejectsNonPositiveRevisionAndInvocationNumber() {
        assertThatThrownBy(() -> new ToolInvocationKey(UUID.randomUUID(), UUID.randomUUID(), 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ToolInvocationKey(UUID.randomUUID(), UUID.randomUUID(), 1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
