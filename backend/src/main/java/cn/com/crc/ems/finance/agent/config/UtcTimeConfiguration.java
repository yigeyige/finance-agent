package cn.com.crc.ems.finance.agent.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Description: 为业务代码提供可替换的 UTC 系统时钟。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Configuration
public class UtcTimeConfiguration {

    /** 注册生产使用的 UTC 时钟，测试可覆盖该 Bean。 */
    @Bean
    public Clock applicationClock() {
        return Clock.systemUTC();
    }
}
