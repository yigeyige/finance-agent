package cn.com.crc.ems.finance.agent.harness.application;

import com.fasterxml.jackson.databind.JsonNode;

/** 结构化输出 schema 端口；实现负责拒绝额外字段和非法结构。 */
public interface JsonSchema<T> {

    String schemaRef();

    T validate(JsonNode value);
}
