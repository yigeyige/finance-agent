package cn.com.crc.ems.finance.agent.harness.infrastructure;

import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.harness.application.HarnessStateSerializer;
import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

/**
 *
 * @Description: 基于项目 Jackson 配置边界创建安全 Harness 状态引用。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public class JacksonHarnessStateSerializer implements HarnessStateSerializer {

    @Override
    public StateReference serialize(String schemaVersion, String json, String digest) {
        return new StateReference(schemaVersion, json, digest);
    }

    @Override
    public String deserialize(StateReference stateReference) {
        if (stateReference == null) {
            throw new IllegalArgumentException("stateReference is required");
        }
        return stateReference.json();
    }
}
