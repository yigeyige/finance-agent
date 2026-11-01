package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.iam.application.AgentUserRepository;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;

/**
 *
 * @Description: 为身份范围内任务分页结果补充工作台展示名称，仅投影只读且无凭据的字段。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-01 00:00:00
 */
@Service
public class TaskListRowService {
    private final AgentUserRepository users;
    private final TaskBusinessSceneSkillRepository associations;
    private final BusinessSceneSkillRepository scenes;
    private final EmsDatabaseConnectionConfigRepository databases;

    public TaskListRowService(AgentUserRepository users, TaskBusinessSceneSkillRepository associations,
            BusinessSceneSkillRepository scenes, EmsDatabaseConnectionConfigRepository databases) {
        this.users = users;
        this.associations = associations;
        this.scenes = scenes;
        this.databases = databases;
    }

    /**
     * @description: 将已完成权限过滤的任务页映射为列表展示页，保留服务端分页元数据。
     * @param: page 已按可信身份筛选的任务页
     * @return: 仅包含可展示字段的只读列表页
     * @author: yigeyige99
     */
    public Page rows(TaskPage page) {
        return new Page(page.items().stream().map(this::row).toList(), page.total(), page.page(), page.pageSize());
    }

    /**
     * @description: 读取任务关联的当前显示名称；关联缺失时返回空名称而非内部标识。
     * @param: task 当前页任务
     * @return: 单条只读列表项
     * @author: yigeyige99
     */
    private Item row(InvestigationTask task) {
        DisplayNames names = displayNames(task);
        return new Item(task.taskId(), task.title(), task.originalQuestion(), task.status().name(),
                task.initiatorUserId(), names.initiatorName(), names.businessSceneName(),
                names.databaseInstanceName(), task.createdAt());
    }

    /**
     * @description: 在上层完成任务权限校验后读取当前展示名称，供列表与详情共用。
     * @param: task 已完成权限检查的任务
     * @return: 发起人、场景与实例的只读展示名称
     * @author: yigeyige99
     */
    public DisplayNames displayNames(InvestigationTask task) {
        String initiatorName = users.findById(task.initiatorUserId()).map(AgentUser::displayName).orElse("未知用户");
        String sceneName = associations.findSkillIdByTaskId(task.taskId())
                .flatMap(scenes::findActiveById).map(BusinessSceneSkill::name).orElse(null);
        String databaseName = task.databaseConfigId() == null ? null : databases.findById(task.databaseConfigId())
                .map(EmsDatabaseConnectionConfig::name).orElse(null);
        return new DisplayNames(initiatorName, sceneName, databaseName);
    }

    public record DisplayNames(String initiatorName, String businessSceneName, String databaseInstanceName) { }
    public record Item(UUID taskId, String title, String description, String status, UUID initiatorUserId,
            String initiatorName, String businessSceneName, String databaseInstanceName, Instant createdAt) { }

    public record Page(List<Item> items, long total, int page, int pageSize) { }
}
