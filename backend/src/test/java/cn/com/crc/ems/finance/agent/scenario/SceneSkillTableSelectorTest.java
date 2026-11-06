package cn.com.crc.ems.finance.agent.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillTableSelector;

/**
 * @Description: 验证场景预填的表名筛选：schema 初筛、日志/临时/备份/日期/MQ/事务排除与空表场景。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 18:00:00
 */
class SceneSkillTableSelectorTest {

    private final SceneSkillTableSelector selector = new SceneSkillTableSelector();

    private static final List<String> TABLES = List.of(
            "basedata.sys_value_set_item",
            "basedata.employee_info",
            "basedata.catalog_item",
            "basedata.oper_log",
            "basedata.tmp_import",
            "basedata.employee_info_bak",
            "basedata.employee_info_20261031",
            "activiti_ems.task_tx",
            "activiti_ems.mq_message"
    );

    @Test
    void narrowsByMentionedSchemaAndExcludesNoiseTables() {
        SceneSkillTableSelector.TableSelection selection = selector.select(TABLES, "核查 basedata 中的员工信息与配置");

        assertThat(selection.schemas()).containsExactly("basedata");
        assertThat(selection.tables()).containsExactly("basedata.catalog_item", "basedata.employee_info",
                "basedata.sys_value_set_item");
        assertThat(selection.excludedCount()).isEqualTo(TABLES.size() - 3);
    }

    @Test
    void keepsOnlyRealTablesWhenNoSchemaMentioned() {
        SceneSkillTableSelector.TableSelection selection = selector.select(TABLES, "核查员工信息");

        assertThat(selection.schemas()).isEmpty();
        assertThat(selection.tables()).containsExactly("basedata.catalog_item", "basedata.employee_info",
                "basedata.sys_value_set_item");
        assertThat(selection.tables()).doesNotContain("basedata.oper_log", "activiti_ems.task_tx",
                "activiti_ems.mq_message", "basedata.tmp_import");
    }

    @Test
    void returnsEmptySelectionWhenAllTablesFiltered() {
        SceneSkillTableSelector.TableSelection selection = selector.select(
                List.of("basedata.oper_log", "basedata.tmp_import", "basedata.order_20261031"), "核查日志");

        assertThat(selection.tables()).isEmpty();
        assertThat(selection.excludedCount()).isEqualTo(3);
    }

    @Test
    void doesNotTreatCatalogAsLogTable() {
        assertThat(selector.select(List.of("basedata.catalog_item"), "核查配置").tables())
                .containsExactly("basedata.catalog_item");
    }
}
