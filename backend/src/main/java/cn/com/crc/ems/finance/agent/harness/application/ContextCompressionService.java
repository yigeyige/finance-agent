package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** 仅组装和校验结构化压缩摘要，不生成事实或复制原始快照。 */
public final class ContextCompressionService {

    private final CompressionConfiguration configuration;

    public ContextCompressionService(CompressionConfiguration configuration) {
        this.configuration = configuration == null ? CompressionConfiguration.defaults() : configuration;
    }

    /**
     * 组装受限压缩摘要并立即对 authoritative 目录执行校验。
     *
     * @param taskId 当前任务 ID。
     * @param references 已确认的业务引用。
     * @param summaries 只允许按引用 ID 索引的摘要文本。
     * @param unknownItems 压缩前后必须一致的未知项。
     * @param authoritativeReferences authoritative 引用目录。
     * @return 通过校验的结构化摘要。
     * @author yigeyige99
     */
    public ContextCompressionSummary compress(UUID taskId, Set<CompressionReference> references,
            Map<String, String> summaries, Set<String> unknownItems,
            Map<String, CompressionReference> authoritativeReferences) {
        return compress(taskId, references, summaries, unknownItems, unknownItems, authoritativeReferences);
    }

    /**
     * 在压缩前后使用不同未知项集合时执行完整对照校验，缺失项不会被静默接受。
     *
     * @param taskId 当前任务 ID。
     * @param references 已确认的业务引用。
     * @param summaries 只允许按引用 ID 索引的摘要文本。
     * @param compressedUnknownItems 压缩结果携带的未知项。
     * @param expectedUnknownItems 压缩前应保留的未知项。
     * @param authoritativeReferences authoritative 引用目录。
     * @return 通过校验的结构化摘要。
     * @author yigeyige99
     */
    public ContextCompressionSummary compress(UUID taskId, Set<CompressionReference> references,
            Map<String, String> summaries, Set<String> compressedUnknownItems,
            Set<String> expectedUnknownItems, Map<String, CompressionReference> authoritativeReferences) {
        ContextCompressionSummary summary = new ContextCompressionSummary(taskId, references, summaries,
                compressedUnknownItems);
        return ContextCompressionValidator.validate(summary, authoritativeReferences, expectedUnknownItems,
                configuration);
    }
}
