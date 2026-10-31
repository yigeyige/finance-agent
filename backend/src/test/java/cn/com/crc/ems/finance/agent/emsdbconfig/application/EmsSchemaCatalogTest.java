package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/** 验证实例结构目录的排除与关键词选表边界。 */
class EmsSchemaCatalogTest {
    @Test
    void excludesRuntimeBackupAndEmptyTablesButKeepsConfiguration() {
        assertThat(EmsSchemaCatalog.includeName("activiti_ems", "act_ru_task")).isFalse();
        assertThat(EmsSchemaCatalog.includeName("basedata", "sys_emp_bak20240223")).isFalse();
        assertThat(EmsSchemaCatalog.includeName("basedata", "t_audit_configuration_20240304")).isFalse();
        assertThat(EmsSchemaCatalog.includeName("basedata", "t_audit_configuration")).isTrue();
        assertThat(EmsSchemaCatalog.includeName("claim", "t_audit_configuration")).isFalse();
    }

    @Test
    void matchesFieldCommentWhenTableCommentIsBlank() {
        var catalog = new EmsSchemaCatalog(List.of(new EmsSchemaTable("basedata", "sys_value_set_item", "",
                List.of(new EmsDatabaseColumnMetadata("VSI_KEY", "varchar(50)", "角色代码")))));
        assertThat(catalog.search("查询角色代码", 10)).extracting(EmsSchemaTable::table)
                .containsExactly("sys_value_set_item");
        assertThat(catalog.search("查询付款渠道", 10)).isEmpty();
    }

    @Test
    void matchedFieldRemainsVisibleWithinTwentyColumnModelBudget() {
        var columns = new java.util.ArrayList<EmsDatabaseColumnMetadata>();
        for (int index = 0; index < 25; index++) {
            columns.add(new EmsDatabaseColumnMetadata("field_" + index, "varchar(50)", "普通字段"));
        }
        columns.add(new EmsDatabaseColumnMetadata("VSI_KEY", "varchar(50)", "角色代码"));
        var table = new EmsSchemaTable("basedata", "sys_value_set_item", "", columns);
        var catalog = new EmsSchemaCatalog(List.of(table));

        assertThat(catalog.relevantColumns(table, "查询角色代码")).hasSize(20)
                .first().extracting(EmsDatabaseColumnMetadata::name).isEqualTo("VSI_KEY");
    }
}
