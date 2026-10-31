package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/** 验证值集表能进入业务处理模型的候选表目录。 */
class EmsDatabaseAssistanceServiceTest {

    @Test
    void includesValueSetTablesAndExcludesUnrelatedSettings() {
        var catalog = List.of(
                new EmsDatabaseTableMetadata("basedata", "sys_value_set", ""),
                new EmsDatabaseTableMetadata("basedata", "sys_value_set_item", ""),
                new EmsDatabaseTableMetadata("basedata", "sys_setting", ""));

        var filtered = EmsDatabaseAssistanceService.coarseFilter(catalog);

        assertThat(filtered).extracting(EmsDatabaseTableMetadata::table)
                .contains("sys_value_set", "sys_value_set_item")
                .doesNotContain("sys_setting");
    }
}
