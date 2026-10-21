package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.application.GuidanceDraftRepository;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.GuidanceDraftEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.GuidanceDraftMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 读写业务处理草稿。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-20 22:00:00
 */
@Repository
public class MybatisGuidanceDraftRepository implements GuidanceDraftRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisGuidanceDraftRepository.class);

    private final GuidanceDraftMapper mapper;

    public MybatisGuidanceDraftRepository(GuidanceDraftMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(UUID taskId, String draftJson, UUID createdBy) {
        GuidanceDraftEntity entity = new GuidanceDraftEntity();
        entity.taskId = taskId;
        entity.draftJson = draftJson;
        entity.createdBy = createdBy;
        entity.createdAt = Instant.now();
        mapper.insert(entity);
    }

    @Override
    public Optional<String> findLatestDraftJsonByTaskId(UUID taskId) {
        List<GuidanceDraftEntity> list = mapper.selectList(new QueryWrapper<GuidanceDraftEntity>()
                .eq("task_id", taskId).orderByDesc("created_at").last("LIMIT 1"));
        LOGGER.info("查询业务处理草稿: taskId={} 命中 {} 条", taskId, list.size());
        return list.stream().findFirst().map(item -> item.draftJson);
    }
}
