package cn.com.crc.ems.finance.agent.scenario.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.scenario.infrastructure.entity.BusinessSceneSkillEntity;
import cn.com.crc.ems.finance.agent.scenario.infrastructure.mapper.BusinessSceneSkillMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus 持久化业务场景 Skill。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisBusinessSceneSkillRepository implements BusinessSceneSkillRepository {

    private final BusinessSceneSkillMapper mapper;

    public MybatisBusinessSceneSkillRepository(BusinessSceneSkillMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<BusinessSceneSkill> findActive() {
        return mapper.selectList(new QueryWrapper<BusinessSceneSkillEntity>().isNull("deleted_at")
                .orderByDesc("updated_at")).stream().map(MybatisBusinessSceneSkillRepository::toDomain).toList();
    }

    @Override
    public Optional<BusinessSceneSkill> findActiveById(UUID skillId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<BusinessSceneSkillEntity>()
                .eq("skill_id", PersistenceUuid.toStorage(skillId)).isNull("deleted_at")))
                .map(MybatisBusinessSceneSkillRepository::toDomain);
    }

    @Override
    public void insert(BusinessSceneSkill skill) {
        mapper.insert(toEntity(skill));
    }

    @Override
    public boolean update(BusinessSceneSkill skill) {
        BusinessSceneSkillEntity entity = toEntity(skill);
        return mapper.update(entity, new UpdateWrapper<BusinessSceneSkillEntity>().eq("skill_id",
                PersistenceUuid.toStorage(skill.skillId())).isNull("deleted_at")) == 1;
    }

    @Override
    public boolean softDelete(UUID skillId, UUID operatorId, Instant deletedAt) {
        return mapper.update(null, new UpdateWrapper<BusinessSceneSkillEntity>().eq("skill_id",
                PersistenceUuid.toStorage(skillId)).isNull("deleted_at").set("deleted_by", operatorId)
                .set("deleted_at", deletedAt).set("updated_by", operatorId).set("updated_at", deletedAt)) == 1;
    }

    private static BusinessSceneSkill toDomain(BusinessSceneSkillEntity entity) {
        return new BusinessSceneSkill(PersistenceUuid.toDomain(entity.skillId), entity.name, entity.investigationPrompt,
                entity.decisionPacketPrompt, entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    private static BusinessSceneSkillEntity toEntity(BusinessSceneSkill skill) {
        BusinessSceneSkillEntity entity = new BusinessSceneSkillEntity();
        entity.skillId = PersistenceUuid.toStorage(skill.skillId());
        entity.name = skill.name();
        entity.investigationPrompt = skill.investigationPrompt();
        entity.decisionPacketPrompt = skill.decisionPacketPrompt();
        entity.createdBy = skill.createdBy();
        entity.createdAt = skill.createdAt();
        entity.updatedBy = skill.updatedBy();
        entity.updatedAt = skill.updatedAt();
        return entity;
    }
}
