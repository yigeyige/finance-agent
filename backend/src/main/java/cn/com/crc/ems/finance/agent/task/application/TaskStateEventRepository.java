package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;

/** 任务状态事件持久化端口。 */
public interface TaskStateEventRepository {

    /** 追加状态事件，不提供更新或删除能力。 */
    TaskStateEvent append(TaskStateEvent event);
}
