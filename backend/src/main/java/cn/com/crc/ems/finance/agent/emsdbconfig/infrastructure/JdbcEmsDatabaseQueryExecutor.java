package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryResult;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseTableMetadata;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseColumnMetadata;
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

    private Connection open(EmsDatabaseConnectionConfig config) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", config.username());
        properties.setProperty("password", cipher.decrypt(config.passwordCiphertext()));
        properties.setProperty("connectTimeout", String.valueOf(config.connectTimeoutSeconds() * 1_000));
        Connection connection = DriverManager.getConnection(config.jdbcUrl(), properties);
        connection.setReadOnly(true);
        return connection;
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
        if (value == null) return null;
        if (value instanceof byte[]) return "[二进制内容已省略]";
        String text = String.valueOf(value);
        return text.length() <= MAX_CELL_LENGTH ? text : text.substring(0, MAX_CELL_LENGTH) + "…";
    }
}
