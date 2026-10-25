package cn.com.crc.ems.finance.agent.shared.domain;

import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 *
 * @Description: 业务 JSON 解析与序列化的统一入口，持有共享默认 JsonMapper 实例，
 * 避免业务模块各自新建 Jackson 实例。审计 canonical 编码仍由 CanonicalJsonCodec 专责。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 10:00:00
 */
public final class JsonCodec {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private JsonCodec() {
    }

    /** 返回线程安全的共享默认 JsonMapper 实例。 */
    public static JsonMapper mapper() {
        return JSON_MAPPER;
    }
}
