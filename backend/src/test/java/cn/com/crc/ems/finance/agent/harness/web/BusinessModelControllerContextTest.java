package cn.com.crc.ems.finance.agent.harness.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** 验证业务模型控制器存在测试构造器时仍可由 Spring 正常装配。 */
@SpringBootTest(properties = "management.health.db.enabled=false")
class BusinessModelControllerContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void createsBusinessModelControllerFromTheApplicationContext() {
        assertThat(applicationContext.getBean(BusinessModelController.class)).isNotNull();
    }
}
