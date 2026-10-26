package cn.com.crc.ems.finance.agent.emsdbconfig.application;

/**
 *
 * @Description: 送入模型的表知识提示词摘要，仅含定位所需字段。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 00:00:00
 */
public record EmsTableKnowledgeHint(String schema, String table, String promptHint) {
}
