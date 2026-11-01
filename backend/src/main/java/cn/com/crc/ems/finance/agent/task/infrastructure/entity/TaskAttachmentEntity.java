package cn.com.crc.ems.finance.agent.task.infrastructure.entity;

import java.time.Instant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 任务附件留痕的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
@TableName("task.task_attachment")
public class TaskAttachmentEntity {
    @TableId(value = "attachment_id", type = IdType.ASSIGN_UUID)
    public String attachmentId;
    public String taskId;
    public String fileName;
    public String contentType;
    public Long sizeBytes;
    public String sha256;
    public byte[] content;
    public String extractedText;
    public Boolean truncated;
    public Instant createdAt;
}
