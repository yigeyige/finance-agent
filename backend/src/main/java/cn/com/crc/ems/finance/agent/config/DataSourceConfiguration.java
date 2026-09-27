package cn.com.crc.ems.finance.agent.config;

import javax.sql.DataSource;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @Description: 为 MyBatis-Plus 持久层提供按项目数据库配置创建的数据源。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 21:10:00
 */
@Configuration
public class DataSourceConfiguration {

    /**
     *
     * @description: 根据受控配置创建连接池数据源，供 MyBatis-Plus Mapper 使用。
     *
     * @param: properties 数据库连接配置。
     * @return: MyBatis-Plus 使用的数据源。
     */
    @Bean(name = "financeAgentApiDataSource", destroyMethod = "close")
    public HikariDataSource financeAgentApiDataSource(FinanceAgentProperties properties) {
        return createDataSource(properties, properties.getDatabase().getConnectionPoolSize(), "finance-agent-api");
    }

    /** 为 Worker 创建独立 Hikari 连接池，避免后台领取耗尽 API 连接。 */
    @Bean(name = "financeAgentWorkerDataSource", destroyMethod = "close")
    public HikariDataSource financeAgentWorkerDataSource(FinanceAgentProperties properties) {
        return createDataSource(properties, properties.getWorker().getConnectionPoolSize(), "finance-agent-worker");
    }

    /** MyBatis 使用按线程角色路由的主数据源，未显式标记时走 API 池。 */
    @Bean
    @Primary
    public DataSource financeAgentDataSource(@Qualifier("financeAgentApiDataSource") HikariDataSource apiDataSource,
            @Qualifier("financeAgentWorkerDataSource") HikariDataSource workerDataSource) {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return DataSourceRoleContext.currentRole();
            }
        };
        routingDataSource.setDefaultTargetDataSource(apiDataSource);
        routingDataSource.setTargetDataSources(Map.of(DataSourceRoleContext.Role.API, apiDataSource,
                DataSourceRoleContext.Role.WORKER, workerDataSource));
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private HikariDataSource createDataSource(FinanceAgentProperties properties, int maximumPoolSize,
            String poolName) {
        FinanceAgentProperties.Database database = properties.getDatabase();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName(poolName);
        dataSource.setJdbcUrl(database.getUrl());
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
        // ASSIGN_UUID 返回 String，而现有 PostgreSQL 主键列保持 uuid；让驱动按目标列推断字符串参数类型。
        dataSource.addDataSourceProperty("stringtype", "unspecified");
        dataSource.setConnectionTimeout(database.getConnectTimeoutSeconds() * 1000L);
        dataSource.setValidationTimeout(database.getValidationTimeoutSeconds() * 1000L);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setInitializationFailTimeout(-1);
        return dataSource;
    }
}
