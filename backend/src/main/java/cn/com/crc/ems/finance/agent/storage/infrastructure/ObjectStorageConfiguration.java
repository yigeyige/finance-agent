package cn.com.crc.ems.finance.agent.storage.infrastructure;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageGateway;

@Configuration(proxyBeanMethods = false)
/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:45
 */
public class ObjectStorageConfiguration {

    @Bean
    @Profile({"local", "test"})
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: properties 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    ObjectStorageGateway temporaryObjectStorageGateway(FinanceAgentProperties properties) {
        FinanceAgentProperties.Storage storage = properties.getStorage();
        String root = storage == null || storage.getTemporaryRoot() == null || storage.getTemporaryRoot().isBlank()
                ? ".runtime/finance-agent/objects" : storage.getTemporaryRoot();
        long maxBytes = storage == null ? 10 * 1024 * 1024L : storage.getMaxBytes();
        return new LocalTemporaryObjectStorageGateway(Path.of(root), maxBytes);
    }

    @Bean
    @Profile("prod")
    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    ObjectStorageGateway disabledObjectStorageGateway() {
        return new DisabledObjectStorageGateway();
    }
}
