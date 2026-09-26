package cn.com.crc.ems.finance.agent.storage.infrastructure;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageGateway;

/** Selects only the controlled temporary implementation outside prod. */
@Configuration(proxyBeanMethods = false)
public class ObjectStorageConfiguration {

    @Bean
    @Profile({"local", "test"})
    ObjectStorageGateway temporaryObjectStorageGateway(FinanceAgentProperties properties) {
        FinanceAgentProperties.Storage storage = properties.getStorage();
        String root = storage == null || storage.getTemporaryRoot() == null || storage.getTemporaryRoot().isBlank()
                ? ".runtime/finance-agent/objects" : storage.getTemporaryRoot();
        long maxBytes = storage == null ? 10 * 1024 * 1024L : storage.getMaxBytes();
        return new LocalTemporaryObjectStorageGateway(Path.of(root), maxBytes);
    }

    @Bean
    @Profile("prod")
    ObjectStorageGateway disabledObjectStorageGateway() {
        return new DisabledObjectStorageGateway();
    }
}
