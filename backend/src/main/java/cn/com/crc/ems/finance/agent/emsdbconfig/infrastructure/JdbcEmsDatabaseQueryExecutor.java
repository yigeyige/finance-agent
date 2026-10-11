package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryResult;
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
