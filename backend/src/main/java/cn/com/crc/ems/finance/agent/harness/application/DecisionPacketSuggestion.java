package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @Description: 生成业务决策包时由模型给出的待决策操作与涉及对象建议。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-26 18:00:00
 */
public record DecisionPacketSuggestion(String action, List<String> targets) {

    public DecisionPacketSuggestion {
        if (action == null || action.isBlank() || action.length() > 2000) {
            throw new IllegalArgumentException("action is invalid");
        }
        targets = targets == null ? List.of() : List.copyOf(targets);
        if (targets.size() > 10) {
            throw new IllegalArgumentException("targets is invalid");
        }
        targets.forEach(value -> {
            if (value == null || value.isBlank() || value.length() > 200) {
                throw new IllegalArgumentException("target is invalid");
            }
        });
    }

    /**
     *
     * @description: 严格解析模型返回的决策包建议，拒绝额外字段和越界内容。
     *
     * @param: value 模型输出中的 JSON 对象
     * @return: 待决策操作与涉及对象
     * @throws: BusinessModelException 输出不符合契约时抛出
     * @author: yigeyige99
     */
    public static DecisionPacketSuggestion fromJson(JsonNode value) {
        if (value == null || !value.isObject()) {
            throw new BusinessModelException("模型返回的决策包建议不符合约定结构");
        }
        try {
            JsonNode action = value.get("action");
            if (action == null || !action.isTextual() || action.textValue().isBlank()
                    || action.textValue().length() > 2000) {
                throw new BusinessModelException("模型返回字段格式不正确: action");
            }
            JsonNode targetsNode = value.get("targets");
            List<String> targets = new ArrayList<>();
            if (targetsNode != null && !targetsNode.isNull()) {
                if (targetsNode.isTextual()) {
                    String text = targetsNode.textValue();
                    if (!text.isBlank()) {
                        targets.add(text);
                    }
                } else if (targetsNode.isArray()) {
                    targetsNode.forEach(item -> {
                        if (item != null && !item.isNull() && item.isTextual()) {
                            String text = item.textValue();
                            if (!text.isBlank() && text.length() <= 200) {
                                targets.add(text);
                            }
                        }
                    });
                }
            }
            return new DecisionPacketSuggestion(action.textValue(), targets);
        } catch (BusinessModelException exception) {
            throw exception;
        } catch (IllegalArgumentException exception) {
            throw new BusinessModelException("模型返回的决策包建议格式不正确", exception);
        }
    }
}
