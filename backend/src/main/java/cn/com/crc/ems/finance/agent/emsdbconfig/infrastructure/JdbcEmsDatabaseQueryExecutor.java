package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryResult;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaCatalog;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsSchemaTable;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsReadOnlySqlGuard;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;

/**
 *
 * @Description: 使用短生命周期 JDBC 连接执行已通过守卫的 EMS 只读查询。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class JdbcEmsDatabaseQueryExecutor implements EmsDatabaseQueryExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcEmsDatabaseQueryExecutor.class);
    private static final int MAX_COLUMNS = 20;
    private static final int MAX_CELL_LENGTH = 300;
    private static final Set<String> ALLOWED_SCHEMAS = Set.of("basedata", "activiti_ems", "account", "budget",
            "payment", "quality", "smart_audit", "mobile");
    private final ModelApiKeyCipher cipher;
    private final EmsReadOnlySqlGuard sqlGuard;

    public JdbcEmsDatabaseQueryExecutor(ModelApiKeyCipher cipher, EmsReadOnlySqlGuard sqlGuard) {
        this.cipher = cipher;
        this.sqlGuard = sqlGuard;
    }

    @Override
    public EmsDatabaseQueryResult execute(EmsDatabaseConnectionConfig config, String sql) {
        String guardedSql = sqlGuard.validate(sql);
        try (Connection connection = open(config); Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(config.queryTimeoutSeconds());
            statement.setMaxRows(config.maxRows());
            try (ResultSet result = statement.executeQuery(guardedSql)) {
                return project(result, config.maxRows());
            }
        } catch (SQLException exception) {
            LOGGER.warn("EMS 数据查询失败", exception);
            throw new IllegalStateException("EMS 数据查询暂时不可用");
        }
    }

    /**
     *
     * @description: 读取固定白名单内的表名称与注释，不接受用户提供的 SQL。
     * @param: config 当前启用的 EMS 连接
     * @return: 允许展示的表元数据
     * @author: yigeyige99
     */
    @Override
    public List<EmsDatabaseTableMetadata> listAllowedTables(EmsDatabaseConnectionConfig config) {
        String sql = "SELECT table_schema, table_name, table_comment FROM information_schema.tables "
                + "WHERE table_type = 'BASE TABLE' AND table_schema IN ('basedata','activiti_ems','account','budget','payment','quality','smart_audit','mobile') "
                + "ORDER BY table_schema, table_name";
        try (Connection connection = open(config); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setQueryTimeout(config.queryTimeoutSeconds());
            try (ResultSet result = statement.executeQuery()) {
                List<EmsDatabaseTableMetadata> values = new ArrayList<>();
                while (result.next()) values.add(new EmsDatabaseTableMetadata(result.getString(1), result.getString(2), result.getString(3)));
                return List.copyOf(values);
            }
        } catch (SQLException exception) {
            LOGGER.warn("EMS 数据目录查询失败", exception);
            throw new IllegalStateException("EMS 数据目录暂时不可用");
        }
    }

    /**
     *
     * @description: 读取指定白名单表的字段元数据，不返回样本数据。
     * @param: config 当前启用的 EMS 连接
     * @param: schema 已校验的业务库名
     * @param: table 已校验的表名
     * @return: 表字段列表
     * @author: yigeyige99
     */
    @Override
    public List<EmsDatabaseColumnMetadata> listColumns(EmsDatabaseConnectionConfig config, String schema, String table) {
        if (!ALLOWED_SCHEMAS.contains(schema)) throw new IllegalArgumentException("不允许读取该 EMS 业务库");
        String sql = "SELECT column_name, column_type, column_comment FROM information_schema.columns "
                + "WHERE table_schema = ? AND table_name = ? ORDER BY ordinal_position";
        try (Connection connection = open(config); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, schema);
            statement.setString(2, table);
            statement.setQueryTimeout(config.queryTimeoutSeconds());
            try (ResultSet result = statement.executeQuery()) {
                List<EmsDatabaseColumnMetadata> values = new ArrayList<>();
                while (result.next()) values.add(new EmsDatabaseColumnMetadata(result.getString(1), result.getString(2), result.getString(3)));
                return List.copyOf(values);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("EMS 表结构暂时不可用");
        }
    }

    /**
     * @description: 手动同步时读取两个配置库的表字段元数据，并以只读存在性查询排除空表。
     * @param: config 管理员选择的数据库实例
     * @return: 不含业务行值的完整候选目录
     * @author: yigeyige99
     */
    @Override
    public EmsSchemaCatalog listConfigurationSchema(EmsDatabaseConnectionConfig config) {
        String tableSql = "SELECT table_schema, table_name, table_comment FROM information_schema.tables "
                + "WHERE table_type = 'BASE TABLE' AND table_schema IN ('basedata','activiti_ems') "
                + "ORDER BY table_schema, table_name";
        String columnSql = "SELECT column_name, column_type, column_comment FROM information_schema.columns "
                + "WHERE table_schema = ? AND table_name = ? ORDER BY ordinal_position";
        try (Connection connection = open(config)) {
            List<EmsDatabaseTableMetadata> candidates = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(tableSql)) {
                statement.setQueryTimeout(config.queryTimeoutSeconds());
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) candidates.add(new EmsDatabaseTableMetadata(rows.getString(1), rows.getString(2), rows.getString(3)));
                }
            }
            List<EmsSchemaTable> tables = new ArrayList<>();
            for (EmsDatabaseTableMetadata item : candidates) {
                if (!EmsSchemaCatalog.includeName(item.schema(), item.table())) continue;
                if (!item.table().matches("[A-Za-z0-9_]{1,128}")) throw new IllegalStateException("EMS 表名不合法");
                String name = "`" + item.schema() + "`.`" + item.table() + "`";
                try (Statement check = connection.createStatement()) {
                    check.setQueryTimeout(config.queryTimeoutSeconds());
                    try (ResultSet exists = check.executeQuery("SELECT 1 FROM " + name + " LIMIT 1")) {
                        if (!exists.next()) continue;
                    }
                }
                List<EmsDatabaseColumnMetadata> columns = new ArrayList<>();
                try (PreparedStatement statement = connection.prepareStatement(columnSql)) {
                    statement.setString(1, item.schema());
                    statement.setString(2, item.table());
                    statement.setQueryTimeout(config.queryTimeoutSeconds());
                    try (ResultSet rows = statement.executeQuery()) {
                        while (rows.next()) columns.add(new EmsDatabaseColumnMetadata(rows.getString(1), rows.getString(2), rows.getString(3)));
                    }
                }
                tables.add(new EmsSchemaTable(item.schema(), item.table(), item.comment(), columns));
            }
            return new EmsSchemaCatalog(tables);
        } catch (SQLException exception) {
            LOGGER.warn("EMS 结构目录同步失败，异常类型={}", exception.getClass().getSimpleName());
            throw new IllegalStateException("数据库实例结构同步失败，请检查连接和只读权限");
        }
    }

    @Override
    public boolean probe(EmsDatabaseConnectionConfig config) {
        try (Connection connection = open(config); Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(config.queryTimeoutSeconds());
            try (ResultSet result = statement.executeQuery("SELECT 1")) {
                return result.next();
            }
        } catch (SQLException exception) {
            return false;
        }
    }

    /** 连接池缓存键为连接配置标识，配置变更时按指纹重建。 */
    private final Map<String, PoolEntry> pools = new ConcurrentHashMap<>();

    private Connection open(EmsDatabaseConnectionConfig config) throws SQLException {
        String key = config.configId().toString();
        String fingerprint = fingerprint(config);
        PoolEntry entry = pools.compute(key, (id, existing) -> {
            if (existing != null && existing.fingerprint().equals(fingerprint)) {
                return existing;
            }
            if (existing != null) {
                existing.pool().close();
            }
            LOGGER.info("建立 EMS 连接池: {}", config.name());
            return new PoolEntry(createPool(config), fingerprint);
        });
        return entry.pool().getConnection();
    }

    private HikariDataSource createPool(EmsDatabaseConnectionConfig config) {
        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(config.jdbcUrl());
        hikari.setUsername(config.username());
        hikari.setPassword(cipher.decrypt(config.passwordCiphertext()));
        hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikari.setMaximumPoolSize(3);
        hikari.setMinimumIdle(1);
        hikari.setConnectionTimeout(config.connectTimeoutSeconds() * 1_000L);
        hikari.setValidationTimeout(5_000L);
        hikari.setIdleTimeout(300_000L);
        hikari.setMaxLifetime(1_800_000L);
        hikari.setReadOnly(true);
        hikari.setInitializationFailTimeout(-1L);
        hikari.setPoolName("ems-" + config.configId());
        return new HikariDataSource(hikari);
    }

    private static String fingerprint(EmsDatabaseConnectionConfig config) {
        return config.jdbcUrl() + '|' + config.username() + '|' + config.passwordCiphertext() + '|'
                + config.connectTimeoutSeconds();
    }

    private record PoolEntry(HikariDataSource pool, String fingerprint) {
    }

    private static EmsDatabaseQueryResult project(ResultSet result, int maxRows) throws SQLException {
        ResultSetMetaData metadata = result.getMetaData();
        int columnCount = Math.min(metadata.getColumnCount(), MAX_COLUMNS);
        List<String> columns = new ArrayList<>(columnCount);
        for (int index = 1; index <= columnCount; index++) columns.add(metadata.getColumnLabel(index));
        List<List<String>> rows = new ArrayList<>();
        while (result.next() && rows.size() < maxRows) {
            List<String> row = new ArrayList<>(columnCount);
            for (int index = 1; index <= columnCount; index++) row.add(cell(result.getObject(index)));
            rows.add(row);
        }
        return new EmsDatabaseQueryResult(columns, rows);
    }

    private static String cell(Object value) {
        if (value == null) return "";
        if (value instanceof byte[]) return "[二进制内容已省略]";
        String text = String.valueOf(value);
        return text.length() <= MAX_CELL_LENGTH ? text : text.substring(0, MAX_CELL_LENGTH) + "…";
    }
}
