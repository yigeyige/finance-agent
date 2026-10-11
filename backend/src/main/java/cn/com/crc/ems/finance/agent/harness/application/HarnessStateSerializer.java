package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.harness.domain.StateReference;

/**
 *
 * @Description: Harness 状态序列化端口，统一校验 schema、摘要和禁止字段。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface HarnessStateSerializer {

    /** 将候选 JSON 编码为安全状态引用。 */
    StateReference serialize(String schemaVersion, String json, String digest);

    /** 从状态引用还原可持久化 JSON 文本。 */
    String deserialize(StateReference stateReference);
}
