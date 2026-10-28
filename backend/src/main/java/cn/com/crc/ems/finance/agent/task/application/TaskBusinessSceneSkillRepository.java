package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @Description: 任务与业务场景 Skill 的稳定关联端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface TaskBusinessSceneSkillRepository {

    /** 读取任务当前关联的 Skill 标识。 */
    Optional<UUID> findSkillIdByTaskId(UUID taskId);

    /** 在创建任务时写入一次不可变的场景选择。 */
    void insert(UUID taskId, UUID skillId, UUID operatorId, Instant createdAt);

    /** 更新任务时删除旧关联，供后续重新插入。 */
    void deleteByTaskId(UUID taskId);
}
