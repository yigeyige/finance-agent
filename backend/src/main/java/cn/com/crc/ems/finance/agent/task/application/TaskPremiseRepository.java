package cn.com.crc.ems.finance.agent.task.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.task.domain.TaskPremise;

/** 任务前提持久化端口。 */
public interface TaskPremiseRepository {

    /** 追加任务前提。 */
    void insert(TaskPremise premise);

    /** 查询任务的前提列表。 */
    List<TaskPremise> findByTaskId(UUID taskId);
}
