package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.source.application.SourceCatalog;

/**
 *
 * @Description: 按来源目录登记的对象级权威顺序协调来源冲突。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class SourceConflictCoordinator {

    /**
     * 
     * @description: 使用目录权威顺序选择唯一来源，无法确定时 fail-closed
     *
     * @param: catalog 来源目录
     * @param: candidates 同一对象的来源候选
     * @return: 已解析或未知冲突结果
     * @author: yigeyige99
     */
    public SourceConflictResult resolve(SourceCatalog catalog, List<SourceCandidate> candidates) {
        if (catalog == null || candidates == null || candidates.isEmpty()
                || candidates.stream().map(SourceCandidate::objectType).distinct().count() != 1
                || candidates.stream().map(SourceCandidate::objectKey).distinct().count() != 1) {
            return unknown(candidates);
        }
        List<String> authority = catalog.authorityFor(candidates.get(0).objectType());
        if (authority.isEmpty()) {
            return unknown(candidates);
        }
        List<Integer> ranks = new ArrayList<>();
        for (SourceCandidate candidate : candidates) {
            int rank = authority.indexOf(candidate.sourceId());
            if (rank < 0) {
                return unknown(candidates);
            }
            ranks.add(rank);
        }
        int best = ranks.stream().mapToInt(Integer::intValue).min().orElse(-1);
        List<SourceCandidate> winners = new ArrayList<>();
        for (int index = 0; index < candidates.size(); index++) {
            if (ranks.get(index) == best) {
                winners.add(candidates.get(index));
            }
        }
        if (winners.size() != 1) {
            return unknown(candidates);
        }
        return new SourceConflictResult(winners.get(0).sourceId(), SourceConflictCode.RESOLVED, candidates);
    }

    private static SourceConflictResult unknown(List<SourceCandidate> candidates) {
        return new SourceConflictResult(null, SourceConflictCode.UNKNOWN_SOURCE_CONFLICT,
                candidates == null ? List.of() : candidates);
    }
}
