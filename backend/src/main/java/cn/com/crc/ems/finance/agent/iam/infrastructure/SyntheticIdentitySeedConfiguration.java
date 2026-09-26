package cn.com.crc.ems.finance.agent.iam.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cn.com.crc.ems.finance.agent.iam.application.SyntheticIdentitySeeder;

/** 仅在 local/test 且显式打开开关时注册合成数据启动器。 */
@Configuration
@Profile({"local", "test"})
@ConditionalOnProperty(prefix = "finance-agent.identity", name = "seed-enabled", havingValue = "true")
public class SyntheticIdentitySeedConfiguration {

    /** 注册一次性的本地测试种子执行器。 */
    @Bean
    CommandLineRunner syntheticIdentitySeedRunner(SyntheticIdentitySeeder seeder) {
        return args -> seeder.seed();
    }
}
