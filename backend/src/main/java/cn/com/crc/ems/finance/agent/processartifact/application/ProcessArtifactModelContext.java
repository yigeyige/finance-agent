package cn.com.crc.ems.finance.agent.processartifact.application;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 * @Description: 文件生成模型输入，只投影指定决策包的固定推荐方案、调查草稿与证据。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 12:40:00
 */
public record ProcessArtifactModelContext(UUID taskId, UUID packetId, int packetRevision,
        JsonNode packetContent, String prompt, String bpmnReference) {

    public ProcessArtifactModelContext(UUID taskId, UUID packetId, int packetRevision,
            JsonNode packetContent, String prompt) {
        this(taskId, packetId, packetRevision, packetContent, prompt, "");
    }

    public ProcessArtifactModelContext {
        if (taskId == null || packetId == null || packetRevision < 1 || packetContent == null
                || !packetContent.isObject() || prompt == null || prompt.isBlank() || prompt.length() > 4000
                || bpmnReference == null || bpmnReference.length() > 100_000) {
            throw new IllegalArgumentException("文件生成输入不完整");
        }
        packetContent = packetContent.deepCopy();
    }

    /** 构造不含可变任务、附件和备选方案的模型输入。 */
    public ObjectNode toInputJson() {
        ObjectNode input = JsonNodeFactory.instance.objectNode();
        input.put("packetId", packetId.toString());
        input.put("packetRevision", packetRevision);
        input.set("recommendedGoal", packetContent.path("recommendedGoal"));
        input.set("guidanceDraft", packetContent.path("guidanceDraft"));
        input.set("evidence", packetContent.path("evidence"));
        if (!bpmnReference.isBlank()) {
            input.put("bpmnReference", bpmnReference);
        }
        return input;
    }
}
