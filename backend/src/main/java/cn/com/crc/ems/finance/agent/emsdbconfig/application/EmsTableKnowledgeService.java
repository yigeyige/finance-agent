package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsTableDataStatus;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsTableKnowledge;
import cn.com.crc.ems.finance.agent.shared.domain.JsonCodec;

/**
 *
 * @Description: 按连接实例组装送入模型的表知识提示词摘要，跳过无数据表。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 00:00:00
 */
@Service
public class EmsTableKnowledgeService {
    private final EmsTableKnowledgeRepository repository;

    public EmsTableKnowledgeService(EmsTableKnowledgeRepository repository) {
        this.repository = repository;
    }

    /**
     *
     * @description: 返回某连接实例下有数据表的提示词摘要。
     *
     * @param: configId 数据库连接实例标识
     * @return: 表知识提示词摘要列表
     * @author: yigeyige99
     */
    public List<EmsTableKnowledgeHint> hints(UUID configId) {
        return repository.findByConfigId(configId).stream()
                .filter(knowledge -> knowledge.dataStatus() == EmsTableDataStatus.DATA)
                .map(EmsTableKnowledgeService::toHint)
                .toList();
    }

    private static EmsTableKnowledgeHint toHint(EmsTableKnowledge knowledge) {
        return new EmsTableKnowledgeHint(knowledge.schema(), knowledge.table(), extractPromptHint(knowledge.knowledge()));
    }

    private static String extractPromptHint(String knowledge) {
        try {
            JsonNode root = JsonCodec.mapper().readTree(knowledge);
            JsonNode hint = root.get("promptHint");
            return hint != null && hint.isTextual() ? hint.asText() : "";
        } catch (Exception ignored) {
            return "";
        }
    }
}
