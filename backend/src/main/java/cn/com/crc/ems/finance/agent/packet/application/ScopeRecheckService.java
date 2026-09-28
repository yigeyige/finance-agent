package cn.com.crc.ems.finance.agent.packet.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.packet.domain.PacketDraftStatus;
import cn.com.crc.ems.finance.agent.packet.domain.ScopeRecheckRecord;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/** 在发布前对批准范围执行全量重查并记录草稿影响。 */
@Service
public class ScopeRecheckService {

    private final ScopeRecheckRepository repository;

    public ScopeRecheckService(ScopeRecheckRepository repository) {
        this.repository = repository;
    }

    /**
     * 
     * @description: 比较批准基线与当前全量结果并持久化重查记录
     *
     * @param: taskId 任务标识
     * @param: scopeRevisionId 批准范围版本
     * @param: baseline 批准时的完整范围快照
     * @param: current 发布前重新枚举的完整范围快照
     * @param: checkedAt 重查时间
     * @param: actorId 审计操作者
     * @return: 重查差异、草稿状态和发布判定
     * @author: yigeyige99
     */
    public ScopeRecheckResult recheck(UUID taskId, UUID scopeRevisionId, List<ScopeSnapshot> baseline,
            List<ScopeSnapshot> current, Instant checkedAt, UUID actorId) {
        if (taskId == null || scopeRevisionId == null || baseline == null || current == null || checkedAt == null) {
            throw new IllegalArgumentException("recheck identity, input and time are required");
        }
        Map<String, ScopeSnapshot> before = index(baseline);
        Map<String, ScopeSnapshot> after = index(current);
        List<ScopeRecheckDiff> diffs = new ArrayList<>();
        before.keySet().stream().sorted().forEach(key -> {
            if (!after.containsKey(key)) {
                diffs.add(new ScopeRecheckDiff(key, Set.of(ScopeChangeType.REMOVED)));
            } else {
                Set<ScopeChangeType> changes = changes(before.get(key), after.get(key));
                if (!changes.isEmpty()) diffs.add(new ScopeRecheckDiff(key, changes));
            }
        });
        after.keySet().stream().filter(key -> !before.containsKey(key)).sorted()
                .forEach(key -> diffs.add(new ScopeRecheckDiff(key, Set.of(ScopeChangeType.ADDED))));
        diffs.sort(Comparator.comparing(ScopeRecheckDiff::objectKey));
        RecheckDecision decision = diffs.isEmpty() ? RecheckDecision.READY_TO_PUBLISH : RecheckDecision.STALE_DRAFT;
        PacketDraftStatus draftStatus = diffs.isEmpty() ? PacketDraftStatus.CURRENT : PacketDraftStatus.STALE;
        String resultDigest = Sha256Digest.hex(diffs.toString());
        ScopeRecheckResult result = new ScopeRecheckResult(taskId, scopeRevisionId, checkedAt, diffs, decision,
                draftStatus, resultDigest);
        String summary = diffs.isEmpty() ? "NONE" : diffs.toString();
        repository.insert(new ScopeRecheckRecord(UUID.randomUUID(), taskId, scopeRevisionId, checkedAt, decision,
                resultDigest, summary, actorId, checkedAt, actorId, checkedAt));
        return result;
    }

    private static Map<String, ScopeSnapshot> index(List<ScopeSnapshot> snapshots) {
        Map<String, ScopeSnapshot> indexed = new HashMap<>();
        snapshots.forEach(snapshot -> {
            if (snapshot == null || indexed.put(snapshot.objectKey(), snapshot) != null) {
                throw new IllegalArgumentException("scope snapshot keys must be unique");
            }
        });
        return indexed;
    }

    private static Set<ScopeChangeType> changes(ScopeSnapshot before, ScopeSnapshot after) {
        EnumSet<ScopeChangeType> changes = EnumSet.noneOf(ScopeChangeType.class);
        if (!before.fieldsDigest().equals(after.fieldsDigest())) changes.add(ScopeChangeType.FIELDS_CHANGED);
        if (before.effectiveClass() != after.effectiveClass()) changes.add(ScopeChangeType.EFFECTIVE_CLASS_CHANGED);
        if (!before.sourceVersion().equals(after.sourceVersion())) changes.add(ScopeChangeType.SOURCE_VERSION_CHANGED);
        if (!before.ruleSetVersion().equals(after.ruleSetVersion())) changes.add(ScopeChangeType.RULE_VERSION_CHANGED);
        if (!before.templateVersion().equals(after.templateVersion())) changes.add(ScopeChangeType.TEMPLATE_VERSION_CHANGED);
        return changes;
    }
}
