package cn.com.crc.ems.finance.agent.task.application;

import cn.com.crc.ems.finance.agent.task.domain.TaskStateEvent;

/**
 *
 * @Description: 任务状态事件持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface TaskStateEventRepository {

    /** 追加状态事件，不提供更新或删除能力。 */
    TaskStateEvent append(TaskStateEvent event);
}
