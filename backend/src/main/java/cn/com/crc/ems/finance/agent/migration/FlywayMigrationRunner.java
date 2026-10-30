package cn.com.crc.ems.finance.agent.migration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:30
 */
public final class FlywayMigrationRunner {

    static final String DB_URL = "FINANCE_AGENT_DB_URL";
    static final String DB_USERNAME = "FINANCE_AGENT_DB_USERNAME";
    static final String DB_PASSWORD = "FINANCE_AGENT_DB_PASSWORD";
    static final String MIGRATION_LOCATION = "classpath:db/migration";
    static final String HISTORY_TABLE = "finance_agent_schema_history";

    private FlywayMigrationRunner() {
    }

    /**
     *
     * @description: 启动当前应用或诊断程序。
     *
     * @param: args 输入参数。
     * @author: yigeyige99
     */
    public static void main(String[] args) {
        if (args.length > 0 && "--help".equals(args[0])) {
            printUsage(System.out);
            return;
        }
        if (args.length > 1 || (args.length == 1 && !"--validate".equals(args[0]))) {
            System.out.println("status=CONFIGURATION_ERROR");
            System.out.println("reason=unsupported_argument");
            System.exit(2);
        }

        boolean validateOnly = args.length == 1;
        int exitCode = execute(System.getenv(), System.out, validateOnly);
        System.exit(exitCode);
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: Map<String 输入参数。
     * @param: environment 输入参数。
     * @param: output 输入参数。
     * @param: validateOnly 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    static int execute(Map<String, String> environment, PrintStream output, boolean validateOnly) {
        final MigrationConfiguration configuration;
        try {
            configuration = MigrationConfiguration.fromEnvironment(environment);
        } catch (MigrationConfigurationException exception) {
            output.println("status=CONFIGURATION_ERROR");
            output.println("key=" + exception.key());
            return 2;
        }

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(configuration.url(), configuration.username(), configuration.password())
                    .locations(MIGRATION_LOCATION)
                    .defaultSchema("public")
                    .table(HISTORY_TABLE)
                    .baselineOnMigrate(false)
                    .outOfOrder(false)
                    .validateMigrationNaming(true)
                    .placeholderReplacement(false)
                    .load();
            if (validateOnly) {
                flyway.validate();
                output.println("status=VALID");
            } else {
                int migrations = flyway.migrate().migrationsExecuted;
                output.println("status=OK");
                output.println("migrationsExecuted=" + migrations);
            }
            return 0;
        } catch (FlywayException exception) {
            // Do not print Flyway's message: it may contain a JDBC URL or vendor details.
            output.println("status=MIGRATION_FAILED");
            return 3;
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: output 输入参数。
     * @author: yigeyige99
     */
    private static void printUsage(PrintStream output) {
        output.println("Finance Agent Flyway migration runner");
        output.println("Required: FINANCE_AGENT_DB_URL, FINANCE_AGENT_DB_USERNAME, FINANCE_AGENT_DB_PASSWORD");
        output.println("Use --validate to validate installed migrations without applying DDL.");
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:30
     */
    record MigrationConfiguration(String url, String username, String password) {

        /**
         *
         * @description: 执行当前职责所需的处理。
         *
         * @param: Map<String 输入参数。
         * @param: environment 输入参数。
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        static MigrationConfiguration fromEnvironment(Map<String, String> environment) {
            Objects.requireNonNull(environment, "environment");
            return new MigrationConfiguration(required(environment, DB_URL),
                    required(environment, DB_USERNAME), required(environment, DB_PASSWORD));
        }

        /**
         *
         * @description: 执行当前职责所需的处理。
         *
         * @param: Map<String 输入参数。
         * @param: environment 输入参数。
         * @param: key 输入参数。
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        private static String required(Map<String, String> environment, String key) {
            String value = environment.get(key);
            if (value == null || value.isBlank()) {
                throw new MigrationConfigurationException(key);
            }
            return value;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:30
     */
    static final class MigrationConfigurationException extends IllegalArgumentException {
        private final String key;

        MigrationConfigurationException(String key) {
            super(key);
            this.key = key;
        }

        /**
         *
         * @description: 执行当前职责所需的处理。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        String key() {
            return key;
        }
    }
}
