package cn.com.crc.ems.finance.agent.emsdbconfig.domain;

import java.util.UUID;

/**
 *
 * @Description: 按数据库连接实例、库与表唯一存储的 EMS 表知识，knowledge 为结构化 JSON 文本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 00:00:00
 */
public record EmsTableKnowledge(UUID configId, String schema, String table, String tableComment,
        EmsTableDataStatus dataStatus, String knowledge) {

    public EmsTableKnowledge {
        if (configId == null || schema == null || schema.isBlank() || table == null || table.isBlank()
                || dataStatus == null || knowledge == null || knowledge.isBlank()) {
            throw new IllegalArgumentException("EMS 表知识不合法");
        }
        tableComment = tableComment == null ? "" : tableComment;
    }
}
