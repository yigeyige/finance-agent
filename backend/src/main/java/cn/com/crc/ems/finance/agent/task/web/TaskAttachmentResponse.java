package cn.com.crc.ems.finance.agent.task.web;

import java.time.Instant;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskAttachment;

/**
 *
 * @Description: 任务附件元数据响应，不返回附件正文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
public record TaskAttachmentResponse(UUID attachmentId, String fileName, String contentType, long sizeBytes,
        String sha256, boolean truncated, Instant createdAt) {

    static TaskAttachmentResponse from(TaskAttachment attachment) {
        return new TaskAttachmentResponse(attachment.attachmentId(), attachment.fileName(),
                attachment.contentType(), attachment.sizeBytes(), attachment.sha256(),
                attachment.truncated(), attachment.createdAt());
    }
}
