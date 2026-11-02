package cn.com.crc.ems.finance.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.PromptTemplateRepository;
import cn.com.crc.ems.finance.agent.harness.infrastructure.StubBusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.infrastructure.DatabaseConfiguredBusinessModelClient;
import cn.com.crc.ems.finance.agent.diagnostics.CapturingBusinessModelClient;
import cn.com.crc.ems.finance.agent.diagnostics.LatestExceptionStore;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelConnectionConfigService;

/**
 *
 * @Description: 根据统一 DeepSeek 配置装配真实或显式 Stub 的业务模型端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-06 23:00:00
 */
@Configuration(proxyBeanMethods = false)
public class BusinessModelConfiguration {

    /** Stub 模式不创建网络客户端；真实模式缺少密钥时启动即安全失败。 */
    @Bean
    public BusinessModelClient businessModelClient(FinanceAgentProperties properties,
            ModelConnectionConfigService configService, ModelApiKeyCipher cipher,
            PromptTemplateRepository promptTemplates, LatestExceptionStore exceptionStore) {
        BusinessModelClient delegate = properties.getDeepSeek().isStub()
                ? new StubBusinessModelClient()
                : new DatabaseConfiguredBusinessModelClient(configService, cipher, promptTemplates);
        return new CapturingBusinessModelClient(delegate, exceptionStore);
    }
}
