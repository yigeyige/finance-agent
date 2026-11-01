package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;
import cn.com.crc.ems.finance.agent.task.application.TaskAttachmentRepository;
import cn.com.crc.ems.finance.agent.task.domain.TaskAttachment;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskAttachmentEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.TaskAttachmentMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus 持久化任务附件留痕。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
@Repository
public class MybatisTaskAttachmentRepository implements TaskAttachmentRepository {

    private final TaskAttachmentMapper mapper;

    public MybatisTaskAttachmentRepository(TaskAttachmentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<TaskAttachment> findByTaskId(UUID taskId) {
        return mapper.selectList(new QueryWrapper<TaskAttachmentEntity>()
                .eq("task_id", PersistenceUuid.toStorage(taskId)).orderByAsc("created_at")).stream()
                .map(MybatisTaskAttachmentRepository::toDomain).toList();
    }

    @Override
    public Optional<TaskAttachment> findById(UUID taskId, UUID attachmentId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<TaskAttachmentEntity>()
                .eq("task_id", PersistenceUuid.toStorage(taskId))
                .eq("attachment_id", PersistenceUuid.toStorage(attachmentId))))
                .map(MybatisTaskAttachmentRepository::toDomain);
    }

    @Override
    public boolean existsByTaskIdAndSha256(UUID taskId, String sha256) {
        return mapper.selectCount(new QueryWrapper<TaskAttachmentEntity>()
                .eq("task_id", PersistenceUuid.toStorage(taskId))
                .eq("sha256", sha256)) > 0;
    }

    @Override
    public void insert(TaskAttachment attachment) {
        mapper.insert(toEntity(attachment));
    }

    private static TaskAttachment toDomain(TaskAttachmentEntity entity) {
        return new TaskAttachment(PersistenceUuid.toDomain(entity.attachmentId),
                PersistenceUuid.toDomain(entity.taskId), entity.fileName, entity.contentType,
                entity.sizeBytes == null ? 0L : entity.sizeBytes, entity.sha256, entity.content,
                entity.extractedText, Boolean.TRUE.equals(entity.truncated), entity.createdAt);
    }

    private static TaskAttachmentEntity toEntity(TaskAttachment attachment) {
        TaskAttachmentEntity entity = new TaskAttachmentEntity();
        entity.attachmentId = PersistenceUuid.toStorage(attachment.attachmentId());
        entity.taskId = PersistenceUuid.toStorage(attachment.taskId());
        entity.fileName = attachment.fileName();
        entity.contentType = attachment.contentType();
        entity.sizeBytes = attachment.sizeBytes();
        entity.sha256 = attachment.sha256();
        entity.content = attachment.content();
        entity.extractedText = attachment.extractedText();
        entity.truncated = attachment.truncated();
        entity.createdAt = attachment.createdAt();
        return entity;
    }
}
