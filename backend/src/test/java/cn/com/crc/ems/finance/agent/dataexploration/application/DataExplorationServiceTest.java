package cn.com.crc.ems.finance.agent.dataexploration.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryResult;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsReadOnlySqlGuard;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;

/** 验证数据探索只复用启用连接、固定元数据与统一 SELECT 守卫。 */
class DataExplorationServiceTest {

    @Test
    void catalogAndColumnsUseEnabledConnectionWithoutExecutingBusinessQuery() {
        EmsDatabaseConnectionConfigService configService = mock(EmsDatabaseConnectionConfigService.class);
        EmsDatabaseQueryExecutor executor = mock(EmsDatabaseQueryExecutor.class);
        EmsDatabaseConnectionConfig config = config();
        when(configService.requireEnabled()).thenReturn(config);
        when(executor.listAllowedTables(config)).thenReturn(List.of(new EmsDatabaseTableMetadata("basedata", "sys_emp", "员工")));
        when(executor.listColumns(config, "basedata", "sys_emp")).thenReturn(List.of(new EmsDatabaseColumnMetadata("emp_id", "varchar(36)", "员工标识")));
        DataExplorationService service = new DataExplorationService(configService, executor, new EmsReadOnlySqlGuard());

        assertThat(service.catalog().dataSourceName()).isEqualTo("本地 EMS");
        assertThat(service.columns("basedata", "sys_emp")).extracting(EmsDatabaseColumnMetadata::name).containsExactly("emp_id");

        verify(executor).listAllowedTables(config);
        verify(executor).listColumns(config, "basedata", "sys_emp");
    }

    @Test
    void queryUsesGuardedSqlAndReturnsBoundedExecutorProjection() {
        EmsDatabaseConnectionConfigService configService = mock(EmsDatabaseConnectionConfigService.class);
        EmsDatabaseQueryExecutor executor = mock(EmsDatabaseQueryExecutor.class);
        EmsDatabaseConnectionConfig config = config();
        String sql = "SELECT emp_id FROM basedata.sys_emp LIMIT 20";
        when(configService.requireEnabled()).thenReturn(config);
        when(executor.execute(config, sql)).thenReturn(new EmsDatabaseQueryResult(List.of("emp_id"), List.of(List.of("E-1"))));
        DataExplorationService service = new DataExplorationService(configService, executor, new EmsReadOnlySqlGuard());

        DataExplorationQueryResult result = service.query(sql);

        assertThat(result.rowCount()).isEqualTo(1);
        assertThat(result.columns()).containsExactly("emp_id");
        verify(executor).execute(config, sql);
    }

    @Test
    void rejectsUnapprovedSchemaBeforeReadingMetadata() {
        DataExplorationService service = new DataExplorationService(mock(EmsDatabaseConnectionConfigService.class),
                mock(EmsDatabaseQueryExecutor.class), new EmsReadOnlySqlGuard());

        assertThatThrownBy(() -> service.columns("information_schema", "tables"))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("请求的 EMS 表不合法");
    }

    @Test
    void hidesMissingEnabledConnectionBehindSafeUnavailableError() {
        EmsDatabaseConnectionConfigService configService = mock(EmsDatabaseConnectionConfigService.class);
        when(configService.requireEnabled()).thenThrow(new IllegalStateException("connection details"));
        DataExplorationService service = new DataExplorationService(configService, mock(EmsDatabaseQueryExecutor.class),
                new EmsReadOnlySqlGuard());

        assertThatThrownBy(service::catalog).isInstanceOf(DomainException.class)
                .hasMessage("数据探索暂时不可用，请检查 EMS 数据库连接");
    }

    private static EmsDatabaseConnectionConfig config() {
        UUID operator = UUID.randomUUID();
        return new EmsDatabaseConnectionConfig(UUID.randomUUID(), "本地 EMS", "jdbc:mysql://localhost:3306", "reader",
                "ciphertext", 5, 10, 20, true, operator, Instant.now(), operator, Instant.now());
    }
}
