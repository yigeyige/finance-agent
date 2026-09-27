package cn.com.crc.ems.finance.agent.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Description: 配置 MyBatis-Plus Mapper 扫描边界，避免把应用层接口误注册为 Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 22:10:00
 */
@Configuration
@MapperScan({
        "cn.com.crc.ems.finance.agent.iam.infrastructure.mapper",
        "cn.com.crc.ems.finance.agent.governance.infrastructure.mapper",
        "cn.com.crc.ems.finance.agent.task.infrastructure.mapper",
        "cn.com.crc.ems.finance.agent.evidence.infrastructure.mapper",
        "cn.com.crc.ems.finance.agent.packet.infrastructure.mapper",
        "cn.com.crc.ems.finance.agent.harness.infrastructure.mapper"
})
public class MybatisPlusConfiguration {
}
