package cn.com.crc.ems.finance.agent.diagnostics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

@Component("database")
/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:28
 */
public final class DatabaseHealthIndicator implements HealthIndicator, DiagnosticProbe {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    private final FinanceAgentProperties properties;
    private final ConnectionOpener connectionOpener;

    @Autowired
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: properties 输入参数。
     * @author: yigeyige99
     */
    public DatabaseHealthIndicator(FinanceAgentProperties properties) {
        this(properties, DriverManager::getConnection);
    }

    DatabaseHealthIndicator(FinanceAgentProperties properties, ConnectionOpener connectionOpener) {
        this.properties = properties;
        this.connectionOpener = connectionOpener;
    }

    @Override
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public String name() {
        return "database";
    }

    @Override
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public DiagnosticComponentStatus check() {
        FinanceAgentProperties.Database database = properties.getDatabase();
        if (database == null || isBlank(database.getUrl()) || isBlank(database.getUsername())
                || isBlank(database.getPassword())) {
            return new DiagnosticComponentStatus(name(), "DOWN", "configuration_missing");
        }
        try (Connection connection = connectionOpener.open(database.getUrl(), connectionProperties(database))) {
            if (connection.isValid(database.getValidationTimeoutSeconds())) {
                return new DiagnosticComponentStatus(name(), "UP", "read_only_probe_ok");
            }
            return new DiagnosticComponentStatus(name(), "DOWN", "connection_invalid");
        } catch (SQLException | RuntimeException exception) {
            DiagnosticFailureTelemetry.log(LOGGER, name(), exception);
            return new DiagnosticComponentStatus(name(), "DOWN", "connection_failed");
        }
    }

    @Override
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Health health() {
        DiagnosticComponentStatus status = check();
        Health.Builder builder = "UP".equals(status.status()) ? Health.up() : Health.down();
        return builder.withDetail("component", status.name()).withDetail("reason", status.reason()).build();
    }

    /**
     *
     * @description: 判断当前配置或状态是否满足条件。
     *
     * @param: value 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: database 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private static Properties connectionProperties(FinanceAgentProperties.Database database) {
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", database.getUsername());
        connectionProperties.setProperty("password", database.getPassword());
        connectionProperties.setProperty("connectTimeout", String.valueOf(database.getConnectTimeoutSeconds()));
        connectionProperties.setProperty("socketTimeout", String.valueOf(database.getSocketTimeoutSeconds()));
        return connectionProperties;
    }

    @FunctionalInterface
    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:28
     */
    interface ConnectionOpener {
        /**
         *
         * @description: 执行当前职责所需的处理。
         *
         * @param: url 输入参数。
         * @param: properties 输入参数。
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        Connection open(String url, Properties properties) throws SQLException;
    }
}
