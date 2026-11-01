package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskAttachment;

/**
 *
 * @Description: 任务附件留痕的持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
public interface TaskAttachmentRepository {

    /** 按任务读取附件元数据，不包含原始字节。 */
    List<TaskAttachment> findByTaskId(UUID taskId);

    /** 按任务和附件标识读取附件。 */
    Optional<TaskAttachment> findById(UUID taskId, UUID attachmentId);

    /** 判断任务内是否已存在相同 SHA-256 的附件。 */
    boolean existsByTaskIdAndSha256(UUID taskId, String sha256);

    /** 新增附件留痕。 */
    void insert(TaskAttachment attachment);
}
