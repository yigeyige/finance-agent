package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

class TaskDomainModelTest {

    @Test
    void versionedJsonRequiresSchemaAndJsonContainer() {
        assertThatThrownBy(() -> new VersionedJson("", "{}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VersionedJson("scope-v1", "not-json"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VersionedJson("scope-v1", "[]").requireObject())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
