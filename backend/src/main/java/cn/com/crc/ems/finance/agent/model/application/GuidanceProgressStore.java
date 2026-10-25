package cn.com.crc.ems.finance.agent.model.application;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 *
 * @Description: 业务处理初稿生成的临时阶段进度缓存，供前端在模型调用期间轮询展示；不持久化、不进入审计。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-20 15:00:00
 */
@Service
public class GuidanceProgressStore {

    private final Map<UUID, String> latestByTask = new ConcurrentHashMap<>();

    /**
     *
     * @description: 记录任务当前的生成阶段文案，后续记录覆盖前一次，单任务仅保留最新一条。
     *
     * @param: taskId 业务处理任务标识
     * @param: stage 面向操作员的阶段描述
     * @author: yigeyige99
     */
    public void record(UUID taskId, String stage) {
        latestByTask.put(taskId, stage);
    }

    /**
     *
     * @description: 读取任务最近一次记录阶段，未记录时返回空。
     *
     * @param: taskId 业务处理任务标识
     * @return: 最近阶段描述或空
     * @author: yigeyige99
     */
    public Optional<String> latest(UUID taskId) {
        return Optional.ofNullable(latestByTask.get(taskId));
    }
}
