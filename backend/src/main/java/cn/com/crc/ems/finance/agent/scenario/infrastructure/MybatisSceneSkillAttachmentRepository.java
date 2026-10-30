package cn.com.crc.ems.finance.agent.scenario.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillAttachmentRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.SceneSkillAttachment;
import cn.com.crc.ems.finance.agent.scenario.infrastructure.entity.SceneSkillAttachmentEntity;
import cn.com.crc.ems.finance.agent.scenario.infrastructure.mapper.SceneSkillAttachmentMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus 持久化业务场景 Skill 附件。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
@Repository
public class MybatisSceneSkillAttachmentRepository implements SceneSkillAttachmentRepository {

    private final SceneSkillAttachmentMapper mapper;

    public MybatisSceneSkillAttachmentRepository(SceneSkillAttachmentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SceneSkillAttachment> findBySkillId(UUID skillId) {
        return mapper.selectList(new QueryWrapper<SceneSkillAttachmentEntity>()
                .eq("skill_id", PersistenceUuid.toStorage(skillId)).orderByAsc("created_at")).stream()
                .map(MybatisSceneSkillAttachmentRepository::toDomain).toList();
    }

    @Override
    public int countBySkillId(UUID skillId) {
        return Math.toIntExact(mapper.selectCount(new QueryWrapper<SceneSkillAttachmentEntity>()
                .eq("skill_id", PersistenceUuid.toStorage(skillId))));
    }

    @Override
    public List<SceneSkillAttachment> findBySkillIdAndFileNames(UUID skillId, List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return List.of();
        }
        return mapper.selectList(new QueryWrapper<SceneSkillAttachmentEntity>()
                .eq("skill_id", PersistenceUuid.toStorage(skillId)).in("file_name", fileNames)).stream()
                .map(MybatisSceneSkillAttachmentRepository::toDomain).toList();
    }

    @Override
    public void insert(SceneSkillAttachment attachment) {
        mapper.insert(toEntity(attachment));
    }

    @Override
    public boolean deleteById(UUID skillId, UUID attachmentId) {
        return mapper.delete(new QueryWrapper<SceneSkillAttachmentEntity>()
                .eq("skill_id", PersistenceUuid.toStorage(skillId))
                .eq("attachment_id", PersistenceUuid.toStorage(attachmentId))) == 1;
    }

    private static SceneSkillAttachment toDomain(SceneSkillAttachmentEntity entity) {
        return new SceneSkillAttachment(PersistenceUuid.toDomain(entity.attachmentId),
                PersistenceUuid.toDomain(entity.skillId), entity.fileName, entity.contentType, entity.summary,
                entity.contentText, entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    private static SceneSkillAttachmentEntity toEntity(SceneSkillAttachment attachment) {
        SceneSkillAttachmentEntity entity = new SceneSkillAttachmentEntity();
        entity.attachmentId = PersistenceUuid.toStorage(attachment.attachmentId());
        entity.skillId = PersistenceUuid.toStorage(attachment.skillId());
        entity.fileName = attachment.fileName();
        entity.contentType = attachment.contentType();
        entity.summary = attachment.summary();
        entity.contentText = attachment.contentText();
        entity.createdBy = attachment.createdBy();
        entity.createdAt = attachment.createdAt();
        entity.updatedBy = attachment.updatedBy();
        entity.updatedAt = attachment.updatedAt();
        return entity;
    }
}
