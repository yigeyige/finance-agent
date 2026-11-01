package cn.com.crc.ems.finance.agent.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cn.com.crc.ems.finance.agent.task.application.TaskPage;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;
import cn.com.crc.ems.finance.agent.task.infrastructure.MybatisInvestigationTaskRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.InvestigationTaskMapper;

/** 验证任务列表分页的 SQL 绑定、范围筛选和准确总数。 */
class MybatisTaskPaginationTest {
    @Test
    void repositoryReturnsBoundedSecondPageAndFilteredTotal() {
        InvestigationTaskMapper mapper = mock(InvestigationTaskMapper.class);
        UUID userId = UUID.randomUUID();
        when(mapper.countPageRows(userId, "READY", "权限")).thenReturn(23L);
        when(mapper.selectPageRows(userId, "READY", "权限", 10, 10L)).thenReturn(List.of(task(userId)));

        TaskPage result = new MybatisInvestigationTaskRepository(mapper)
                .findPageByInitiator(userId, 2, 10, TaskStatus.READY, "权限");

        assertThat(result.total()).isEqualTo(23);
        assertThat(result.page()).isEqualTo(2);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.items()).hasSize(1);
        verify(mapper).countPageRows(userId, "READY", "权限");
        verify(mapper).selectPageRows(userId, "READY", "权限", 10, 10L);
    }

    @Test
    void mapperBindsSameFiltersAndLimitsPageInSql() {
        Configuration configuration = new Configuration();
        configuration.addMapper(InvestigationTaskMapper.class);
        UUID userId = UUID.randomUUID();
        Map<String, Object> parameters = Map.of("initiatorUserId", userId, "status", "READY",
                "keyword", "权限", "limit", 10, "offset", 10L);
        String mapperName = InvestigationTaskMapper.class.getName();
        String countSql = configuration.getMappedStatement(mapperName + ".countPageRows")
                .getBoundSql(parameters).getSql();
        String pageSql = configuration.getMappedStatement(mapperName + ".selectPageRows")
                .getBoundSql(parameters).getSql();

        assertThat(countSql).contains("initiator_user_id = ?", "status = ?", "title LIKE CONCAT('%', ?, '%')");
        assertThat(pageSql).contains("initiator_user_id = ?", "status = ?", "title LIKE CONCAT('%', ?, '%')",
                "ORDER BY created_at DESC, task_id DESC", "LIMIT ? OFFSET ?");
    }

    @Test
    void administratorWithoutFiltersKeepsBoundedPageAndCount() {
        Configuration configuration = new Configuration();
        configuration.addMapper(InvestigationTaskMapper.class);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("initiatorUserId", null);
        parameters.put("status", null);
        parameters.put("keyword", null);
        parameters.put("limit", 20);
        parameters.put("offset", 20L);
        String mapperName = InvestigationTaskMapper.class.getName();
        String countSql = configuration.getMappedStatement(mapperName + ".countPageRows")
                .getBoundSql(parameters).getSql();
        String pageSql = configuration.getMappedStatement(mapperName + ".selectPageRows")
                .getBoundSql(parameters).getSql();

        assertThat(countSql).doesNotContain("WHERE", "LIMIT");
        assertThat(pageSql).doesNotContain("WHERE").contains("LIMIT ? OFFSET ?");
    }

    @Test
    void invalidPageIsRejectedBeforeQuery() {
        InvestigationTaskMapper mapper = mock(InvestigationTaskMapper.class);
        MybatisInvestigationTaskRepository repository = new MybatisInvestigationTaskRepository(mapper);

        assertThatThrownBy(() -> repository.findPage(0, 10, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> repository.findPage(1, 101, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(mapper);
    }

    private static InvestigationTaskEntity task(UUID userId) {
        InvestigationTaskEntity entity = new InvestigationTaskEntity();
        entity.taskId = UUID.randomUUID().toString();
        entity.title = "测试任务";
        entity.originalQuestion = "测试业务说明";
        entity.initiatorUserId = userId;
        entity.status = "READY";
        entity.runtimeBundleId = UUID.randomUUID();
        entity.createdAt = Instant.parse("2026-11-01T00:00:00Z");
        entity.updatedAt = entity.createdAt;
        entity.rowVersion = 0L;
        return entity;
    }
}
