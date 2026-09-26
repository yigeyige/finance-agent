package cn.com.crc.ems.finance.agent.iam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import cn.com.crc.ems.finance.agent.iam.application.SyntheticIdentitySeeder;
import cn.com.crc.ems.finance.agent.iam.infrastructure.SyntheticIdentitySeedConfiguration;

class SyntheticIdentitySeedConfigurationTest {

    @Test
    void productionProfileDoesNotRegisterSeedRunner() {
        new ApplicationContextRunner()
                .withUserConfiguration(SyntheticIdentitySeedConfiguration.class)
                .withBean(SyntheticIdentitySeeder.class, () -> mock(SyntheticIdentitySeeder.class))
                .withPropertyValues("spring.profiles.active=prod",
                        "finance-agent.identity.seed-enabled=true")
                .run(context -> assertThat(context).doesNotHaveBean("syntheticIdentitySeedRunner"));
    }

    @Test
    void localProfileRequiresExplicitSeedSwitch() {
        new ApplicationContextRunner()
                .withUserConfiguration(SyntheticIdentitySeedConfiguration.class)
                .withBean(SyntheticIdentitySeeder.class, () -> mock(SyntheticIdentitySeeder.class))
                .withPropertyValues("spring.profiles.active=local",
                        "finance-agent.identity.seed-enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean("syntheticIdentitySeedRunner"));
    }
}
