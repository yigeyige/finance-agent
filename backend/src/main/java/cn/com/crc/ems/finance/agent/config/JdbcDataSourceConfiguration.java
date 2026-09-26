package cn.com.crc.ems.finance.agent.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @Description: 为 Spring JDBC 仓储提供按项目数据库配置创建的数据源和模板。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 21:10:00
 */
@Configuration
public class JdbcDataSourceConfiguration {

    /**
     *
     * @description: 根据受控配置创建非连接池数据源，连接在仓储实际访问时建立。
     *
     * @param: properties 数据库连接配置。
     * @return: Spring JDBC 使用的数据源。
     */
    @Bean
    public DataSource financeAgentDataSource(FinanceAgentProperties properties) {
        FinanceAgentProperties.Database database = properties.getDatabase();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(database.getUrl());
        dataSource.setUsername(database.getUsername());
        dataSource.setPassword(database.getPassword());
        return dataSource;
    }

    /**
     *
     * @description: 创建统一的参数化 JDBC 模板供基础设施仓储复用。
     *
     * @param: dataSource 项目数据库数据源。
     * @return: JDBC 操作模板。
     */
    @Bean
    public JdbcTemplate financeAgentJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
