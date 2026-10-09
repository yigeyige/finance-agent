package cn.com.crc.ems.finance.agent.scenario.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;

/** 业务场景 Skill 的持久化端口，屏蔽软删除实现细节。 */
public interface BusinessSceneSkillRepository {

    /** 查询全部当前有效的业务场景 Skill。 */
    List<BusinessSceneSkill> findActive();

    /** 按标识查询当前有效的业务场景 Skill。 */
    Optional<BusinessSceneSkill> findActiveById(UUID skillId);

    /** 新增业务场景 Skill。 */
    void insert(BusinessSceneSkill skill);

    /** 更新当前有效的业务场景 Skill。 */
    boolean update(BusinessSceneSkill skill);

    /** 软删除当前有效的业务场景 Skill。 */
    boolean softDelete(UUID skillId, UUID operatorId, Instant deletedAt);
}
