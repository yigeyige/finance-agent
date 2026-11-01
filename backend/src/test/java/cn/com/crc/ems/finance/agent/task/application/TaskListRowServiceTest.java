package cn.com.crc.ems.finance.agent.task.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.iam.application.AgentUserRepository;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillRepository;
import cn.com.crc.ems.finance.agent.scenario.domain.BusinessSceneSkill;
import cn.com.crc.ems.finance.agent.task.domain.InvestigationTask;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 验证任务列表只读字段映射，不暴露数据库连接详情。 */
class TaskListRowServiceTest {
    @Test
    void mapsReadableNamesAndDescription() {
        AgentUserRepository users = mock(AgentUserRepository.class);
        TaskBusinessSceneSkillRepository associations = mock(TaskBusinessSceneSkillRepository.class);
        BusinessSceneSkillRepository scenes = mock(BusinessSceneSkillRepository.class);
        EmsDatabaseConnectionConfigRepository databases = mock(EmsDatabaseConnectionConfigRepository.class);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID sceneId = UUID.randomUUID();
        UUID databaseId = UUID.randomUUID();
        AgentUser user = mock(AgentUser.class);
        BusinessSceneSkill scene = mock(BusinessSceneSkill.class);
        EmsDatabaseConnectionConfig database = mock(EmsDatabaseConnectionConfig.class);
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(user.displayName()).thenReturn("张三");
        when(associations.findSkillIdByTaskId(taskId)).thenReturn(Optional.of(sceneId));
        when(scenes.findActiveById(sceneId)).thenReturn(Optional.of(scene));
        when(scene.name()).thenReturn("凭证核对");
        when(databases.findById(databaseId)).thenReturn(Optional.of(database));
        when(database.name()).thenReturn("共享库");
        InvestigationTask task = task(taskId, userId, databaseId);

        TaskListRowService.Item item = new TaskListRowService(users, associations, scenes, databases)
                .rows(new TaskPage(List.of(task), 1, 1, 10)).items().get(0);

        assertThat(item.initiatorName()).isEqualTo("张三");
        assertThat(item.description()).isEqualTo("核对跨月凭证差异");
        assertThat(item.businessSceneName()).isEqualTo("凭证核对");
        assertThat(item.databaseInstanceName()).isEqualTo("共享库");
    }

    @Test
    void missingOptionalAssociationsRemainEmpty() {
        AgentUserRepository users = mock(AgentUserRepository.class);
        TaskBusinessSceneSkillRepository associations = mock(TaskBusinessSceneSkillRepository.class);
        BusinessSceneSkillRepository scenes = mock(BusinessSceneSkillRepository.class);
        EmsDatabaseConnectionConfigRepository databases = mock(EmsDatabaseConnectionConfigRepository.class);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(users.findById(userId)).thenReturn(Optional.empty());
        when(associations.findSkillIdByTaskId(taskId)).thenReturn(Optional.empty());

        TaskListRowService.Item item = new TaskListRowService(users, associations, scenes, databases)
                .rows(new TaskPage(List.of(task(taskId, userId, null)), 1, 1, 10)).items().get(0);

        assertThat(item.initiatorName()).isEqualTo("未知用户");
        assertThat(item.businessSceneName()).isNull();
        assertThat(item.databaseInstanceName()).isNull();
    }

    private static InvestigationTask task(UUID taskId, UUID userId, UUID databaseId) {
        Instant now = Instant.parse("2026-11-01T00:00:00Z");
        return new InvestigationTask(taskId, "核对凭证", "核对跨月凭证差异", userId, TaskStatus.DRAFT,
                UUID.randomUUID(), databaseId, null, null, now, now, 0, null, userId, userId);
    }
}
