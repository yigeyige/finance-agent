package cn.com.crc.ems.finance.agent.businesspacket.web;

import java.time.Instant;

import com.fasterxml.jackson.databind.json.JsonMapper;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;

/**
 *
 * @Description: 业务处理证据工作区累积证据响应，evidence 为当轮证据数组。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public record BusinessEvidenceAccumResponse(int roundNo, Object evidence, Instant createdAt) {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    /** 由单轮累积记录构造响应。 */
    public static BusinessEvidenceAccumResponse from(BusinessEvidenceAccum accum) {
        return new BusinessEvidenceAccumResponse(accum.roundNo(), parse(accum.evidenceJson()), accum.createdAt());
    }

    private static Object parse(String json) {
        try {
            return JSON_MAPPER.readValue(json, Object.class);
        } catch (Exception exception) {
            throw new IllegalStateException("证据工作区数据不可解析", exception);
        }
    }
}
