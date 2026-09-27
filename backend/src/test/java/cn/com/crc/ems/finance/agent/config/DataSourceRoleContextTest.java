package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DataSourceRoleContextTest {

    @Test
    void workerRoleIsScopedAndRestoredAfterCallback() {
        assertThat(DataSourceRoleContext.currentRole()).isEqualTo(DataSourceRoleContext.Role.API);
        String result = DataSourceRoleContext.withWorker(() -> {
            assertThat(DataSourceRoleContext.currentRole()).isEqualTo(DataSourceRoleContext.Role.WORKER);
            return "done";
        });
        assertThat(result).isEqualTo("done");
        assertThat(DataSourceRoleContext.currentRole()).isEqualTo(DataSourceRoleContext.Role.API);
    }
}
