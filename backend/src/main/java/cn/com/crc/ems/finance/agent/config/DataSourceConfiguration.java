package cn.com.crc.ems.finance.agent.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Bean
    public DataSource financeAgentDataSource(FinanceAgentProperties properties) {
        FinanceAgentProperties.Database database = properties.getDatabase();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(database.getUrl());
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
        dataSource.setConnectionTimeout(database.getConnectTimeoutSeconds() * 1000L);
        dataSource.setValidationTimeout(database.getValidationTimeoutSeconds() * 1000L);
        dataSource.setInitializationFailTimeout(-1);
        return dataSource;
    }
}
