package cn.com.crc.ems.finance.agent.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 为业务代码提供可替换的 UTC 系统时钟。 */
@Configuration
public class UtcTimeConfiguration {

    /** 注册生产使用的 UTC 时钟，测试可覆盖该 Bean。 */
    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
