package cn.com.crc.ems.finance.agent.harness.application;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 结构化输出 schema 端口；实现负责拒绝额外字段和非法结构。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface JsonSchema<T> {

    String schemaRef();

    T validate(JsonNode value);
}
