package cn.com.crc.ems.finance.agent.task.application;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * @Description: 业务处理草稿持久化端口，仅保存草稿全文并读取最新一份。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-20 22:00:00
 */
public interface GuidanceDraftRepository {

    /** 追加保存一份草稿全文，历史版本保留。 */
    void save(UUID taskId, String draftJson, UUID createdBy);

    /** 读取任务最新一份草稿全文，不存在时返回空。 */
    Optional<String> findLatestDraftJsonByTaskId(UUID taskId);
}
