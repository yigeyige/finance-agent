package cn.com.crc.ems.finance.agent.rules.application;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.com.crc.ems.finance.agent.evidence.domain.EffectiveClass;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 使用项目统一 canonical JSON 和摘要工具规范化规则输入输出。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public class RuleInputNormalizer {

    /**
     * 
     * @description: 按快照 ID 排序并生成规则输入摘要，保证字段顺序不影响复算
     *
     * @param: input 获准快照输入
     * @return: 规范化输入及摘要
     * @author: yigeyige99
     */
    public NormalizedRuleInput normalize(RuleInput input) {
        if (input == null) {
            throw new IllegalArgumentException("rule input is required");
        }
        List<ApprovedSnapshot> snapshots = input.snapshots().stream()
                .sorted(Comparator.comparing(snapshot -> snapshot.snapshotId().toString())).toList();
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        snapshots.forEach(snapshot -> {
            ObjectNode item = JsonNodeFactory.instance.objectNode();
            item.put("snapshotId", snapshot.snapshotId().toString());
            item.set("payload", snapshot.retainedPayload());
            array.add(item);
        });
        String canonical = CanonicalJsonCodec.canonicalize(array);
        ArrayNode snapshotIds = JsonNodeFactory.instance.arrayNode();
        snapshots.forEach(snapshot -> snapshotIds.add(snapshot.snapshotId().toString()));
        return new NormalizedRuleInput(snapshots, Sha256Digest.hex(canonical), CanonicalJsonCodec.canonicalize(snapshotIds));
    }

    /** 计算规则输出的稳定摘要。 */
    public String outputDigest(RuleFindingDraft draft) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("findingType", draft.findingType());
        output.put("statement", draft.statement());
        output.put("effectiveClass", draft.effectiveClass().name());
        return Sha256Digest.hex(CanonicalJsonCodec.canonicalize(output));
    }
}
