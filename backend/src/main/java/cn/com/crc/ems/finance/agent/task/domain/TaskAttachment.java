package cn.com.crc.ems.finance.agent.task.domain;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @Description: 任务上传附件留痕记录，保存原始文件、模型文本投影和内容摘要。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
public record TaskAttachment(UUID attachmentId, UUID taskId, String fileName, String contentType,
        long sizeBytes, String sha256, byte[] content, String extractedText, boolean truncated,
        Instant createdAt) {

    public TaskAttachment {
        content = content == null ? new byte[0] : content.clone();
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
