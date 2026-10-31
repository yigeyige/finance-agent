package cn.com.crc.ems.finance.agent.scenario.application;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.shared.domain.UtcTime;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/**
 *
 * @Description: 维护管理员可复用的业务场景 Skill。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 23:00:00
 */
@Service
public class BusinessSceneSkillService {

    private final BusinessSceneSkillRepository repository;
    private final Clock applicationClock;

    public BusinessSceneSkillService(BusinessSceneSkillRepository repository, Clock applicationClock) {
        this.repository = repository;
        this.applicationClock = applicationClock;
    }

    /** 查询当前有效的业务场景 Skill。 */
    @Transactional(readOnly = true)
    public List<BusinessSceneSkill> list() {
        return repository.findActive();
    }

    /** 新增业务场景 Skill。 */
    @Transactional
    public BusinessSceneSkill create(String name, String investigationPrompt, String decisionPacketPrompt,
            UUID operatorId) {
        return create(name, investigationPrompt, decisionPacketPrompt, null, operatorId);
    }

    /** 新增包含可选文件生成提示词的业务场景 Skill。 */
    @Transactional
    public BusinessSceneSkill create(String name, String investigationPrompt, String decisionPacketPrompt,
            String processArtifactPrompt, UUID operatorId) {
        BusinessSceneSkill skill = BusinessSceneSkill.create(UuidGenerator.newUuid(), name, investigationPrompt,
                decisionPacketPrompt, processArtifactPrompt, operatorId, UtcTime.now(applicationClock));
        repository.insert(skill);
        return skill;
    }

    /** 更新一个当前有效的业务场景 Skill。 */
    @Transactional
    public BusinessSceneSkill update(UUID skillId, String name, String investigationPrompt, String decisionPacketPrompt,
            UUID operatorId) {
        String existingPrompt = repository.findActiveById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("business scene skill is unavailable"))
                .processArtifactPrompt();
        return update(skillId, name, investigationPrompt, decisionPacketPrompt, existingPrompt, operatorId);
    }

    /** 更新可选文件生成提示词并保留场景审计字段。 */
    @Transactional
    public BusinessSceneSkill update(UUID skillId, String name, String investigationPrompt, String decisionPacketPrompt,
            String processArtifactPrompt, UUID operatorId) {
        BusinessSceneSkill current = repository.findActiveById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("business scene skill is unavailable"));
        BusinessSceneSkill revised = current.revise(name, investigationPrompt, decisionPacketPrompt,
                processArtifactPrompt, operatorId,
                UtcTime.now(applicationClock));
        if (!repository.update(revised)) {
            throw new IllegalStateException("business scene skill was changed concurrently");
        }
        return revised;
    }

    /** 软删除一个当前有效的业务场景 Skill。 */
    @Transactional
    public void delete(UUID skillId, UUID operatorId) {
        if (!repository.softDelete(skillId, operatorId, UtcTime.now(applicationClock))) {
            throw new IllegalArgumentException("business scene skill is unavailable");
        }
    }
}
