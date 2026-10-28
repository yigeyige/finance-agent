package cn.com.crc.ems.finance.agent.dataexploration.application;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsReadOnlySqlGuard;
import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;

/**
 *
 * @Description: 编排管理员发起的 EMS 元数据浏览与受控只读查询，不调用业务模型。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
@Service
public class DataExplorationService {
    private static final Set<String> ALLOWED_SCHEMAS = Set.of("basedata", "activiti_ems", "account", "budget",
            "payment", "quality", "smart_audit", "mobile");
    private final EmsDatabaseConnectionConfigService configService;
    private final EmsDatabaseQueryExecutor executor;
    private final EmsReadOnlySqlGuard sqlGuard;

    public DataExplorationService(EmsDatabaseConnectionConfigService configService, EmsDatabaseQueryExecutor executor,
            EmsReadOnlySqlGuard sqlGuard) {
        this.configService = configService;
        this.executor = executor;
        this.sqlGuard = sqlGuard;
    }

    /**
     *
     * @description: 返回当前启用连接的安全表目录。
     * @return: 数据源名称、行数限制和表目录
     * @author: yigeyige99
     */
    public DataExplorationCatalog catalog(UUID configId) {
        try {
            var config = configService.resolveEnabled(configId);
            return new DataExplorationCatalog(config.name(), config.maxRows(), executor.listAllowedTables(config));
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw unavailable();
        }
    }

    /**
     *
     * @description: 返回一个允许 EMS 表的字段结构。
     * @param: schema 业务库名称
     * @param: table 表名称
     * @return: 字段结构列表
     * @author: yigeyige99
     */
    public List<EmsDatabaseColumnMetadata> columns(UUID configId, String schema, String table) {
        if (!ALLOWED_SCHEMAS.contains(schema) || table == null || !table.matches("[A-Za-z0-9_]{1,128}")) {
            throw new IllegalArgumentException("请求的 EMS 表不合法");
        }
        try {
            return executor.listColumns(configService.resolveEnabled(configId), schema, table);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw unavailable();
        }
    }

    /**
     *
     * @description: 执行管理员提交且已通过统一只读守卫的 SQL。
     * @param: sql 单条库名限定 SELECT
     * @return: 受限查询结果和耗时
     * @author: yigeyige99
     */
    public DataExplorationQueryResult query(UUID configId, String sql) {
        String guardedSql = sqlGuard.validate(sql);
        try {
            var config = configService.resolveEnabled(configId);
            long started = System.nanoTime();
            var result = executor.execute(config, guardedSql);
            long elapsedMillis = java.time.Duration.ofNanos(System.nanoTime() - started).toMillis();
            return new DataExplorationQueryResult(result.columns(), result.rows(), result.rows().size(), elapsedMillis);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            throw unavailable();
        }
    }

    /** 将连接缺失或 JDBC 安全失败收敛为不泄露内部细节的探索不可用错误。 */
    private static DomainException unavailable() {
        return new DomainException(DomainErrorCode.DATA_EXPLORATION_UNAVAILABLE);
    }
}
