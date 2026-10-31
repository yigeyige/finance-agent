package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;

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

    @Test
    void synchronizedCatalogWithoutKeywordMatchStopsBeforeModelAndSourceQuery() {
        var configService = mock(EmsDatabaseConnectionConfigService.class);
        var catalogService = mock(EmsSchemaCatalogService.class);
        var executor = mock(EmsDatabaseQueryExecutor.class);
        var model = mock(BusinessModelClient.class);
        var configId = UUID.randomUUID();
        when(configService.resolveEnabled(configId)).thenReturn(mock(EmsDatabaseConnectionConfig.class));
        when(catalogService.find(configId)).thenReturn(Optional.of(new EmsSchemaCatalog(List.of())));
        var service = new EmsDatabaseAssistanceService(configService, mock(EmsReadOnlySqlGuard.class), executor, catalogService);
        var context = new BusinessGuidanceContext(UUID.randomUUID(), "角色调查", "查询角色代码", null, null);

        assertThatThrownBy(() -> service.collect(model, context, configId, ignored -> { }))
                .isInstanceOf(EmsSchemaCatalogNoMatchException.class);
        verifyNoInteractions(model, executor);
    }

    @Test
    void synchronizedCatalogProvidesFieldsLocallyAndKeepsLiveDataQuery() {
        var configService = mock(EmsDatabaseConnectionConfigService.class);
        var catalogService = mock(EmsSchemaCatalogService.class);
        var executor = mock(EmsDatabaseQueryExecutor.class);
        var model = mock(BusinessModelClient.class);
        var guard = mock(EmsReadOnlySqlGuard.class);
        var configId = UUID.randomUUID();
        var config = mock(EmsDatabaseConnectionConfig.class);
        when(configService.resolveEnabled(configId)).thenReturn(config);
        when(catalogService.find(configId)).thenReturn(Optional.of(new EmsSchemaCatalog(List.of(
                new EmsSchemaTable("basedata", "sys_value_set_item", "角色值集",
                        List.of(new EmsDatabaseColumnMetadata("VSI_KEY", "varchar(50)", "角色代码")))))));
        var context = new BusinessGuidanceContext(UUID.randomUUID(), "角色调查", "查询角色代码", null, null);
        when(model.selectRelevantTables(eq(context), any(), eq(10))).thenReturn(List.of("basedata.sys_value_set_item"));
        String sql = "SELECT VSI_KEY FROM basedata.sys_value_set_item LIMIT 1";
        when(model.planEmsDatabaseQuery(eq(context), any(), any(), any()))
                .thenReturn(Optional.of(new cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryRequest("核对角色", sql)))
                .thenReturn(Optional.empty());
        when(guard.validate(sql)).thenReturn(sql);
        when(executor.execute(config, sql)).thenReturn(new EmsDatabaseQueryResult(List.of("VSI_KEY"), List.of(List.of("ZYMD"))));
        var service = new EmsDatabaseAssistanceService(configService, guard, executor, catalogService);

        assertThat(service.collect(model, context, configId, ignored -> { })).hasSize(1);
        verify(executor).execute(config, sql);
        verify(executor, never()).listAllowedTables(any());
        verify(executor, never()).listColumns(any(), any(), any());
    }
}
