package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.task.domain.VersionedJson;

class TaskDomainModelTest {

    @Test
    void versionedJsonRequiresSchemaAndStrictJsonContainer() {
        assertThatThrownBy(() -> new VersionedJson("", "{}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VersionedJson("scope-v1", "not-json"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VersionedJson("scope-v1", "{invalid}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VersionedJson("scope-v1", "{} {}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VersionedJson("scope-v1", "[]").requireObject())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void versionedJsonAcceptsValidObjectAndArray() {
        assertThatCode(() -> new VersionedJson("scope-v1", "{\"field\": [1, true]}"))
                .doesNotThrowAnyException();
        assertThatCode(() -> new VersionedJson("scope-v1", "[1, {\"field\": \"value\"}]"))
                .doesNotThrowAnyException();
    }

    @Test
    void requireObjectAcceptsOnlyJsonObject() {
        assertThatCode(() -> new VersionedJson("scope-v1", "{\"field\": \"value\"}").requireObject())
                .doesNotThrowAnyException();
    }
}
