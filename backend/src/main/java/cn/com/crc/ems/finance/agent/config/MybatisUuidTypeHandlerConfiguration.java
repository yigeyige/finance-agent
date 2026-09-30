package cn.com.crc.ems.finance.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;

/**
 *
 * @Description: 注册 MyBatis-Plus 使用的 PostgreSQL UUID 类型映射。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-30 00:35:00
 */
@Configuration
public class MybatisUuidTypeHandlerConfiguration {

    /**
     *
     * @description: 将 PostgreSQL UUID 处理器加入 MyBatis 类型处理器注册表。
     *
     * @return: MyBatis-Plus 配置定制器。
     * @author: yigeyige99
     */
    @Bean
    public ConfigurationCustomizer postgresUuidTypeHandlerCustomizer() {
        return configuration -> configuration.getTypeHandlerRegistry().register(PostgresUuidTypeHandler.class);
    }
}
