package cn.com.crc.ems.finance.agent.businessintake.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @Description: AI 预填请求体，只接受自由文本，拒绝未知字段。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:30:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record BusinessIntakeDraftRequest(String text) {
}
