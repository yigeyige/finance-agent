package cn.com.crc.ems.finance.agent.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/** 验证 MyBatis-Plus Mapper 扫描边界和旧 JdbcTemplate bean 已移除。 */
@SpringBootTest(properties = "management.health.db.enabled=false")
class MybatisPlusPersistenceContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void scansPersistenceMappersWithoutJdbcTemplateBean() {
        assertThat(applicationContext.containsBean("agentUserMapper")).isTrue();
        assertThat(applicationContext.containsBean("runtimeBundleMapper")).isTrue();
        assertThat(applicationContext.containsBean("investigationTaskMapper")).isTrue();
        assertThat(applicationContext.containsBean("factSnapshotMapper")).isTrue();
        assertThat(applicationContext.containsBean("coverageRecordMapper")).isTrue();
        assertThat(applicationContext.containsBean("findingMapper")).isTrue();
        assertThat(applicationContext.containsBean("financeAgentJdbcTemplate")).isFalse();
    }
}
