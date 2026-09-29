package cn.com.crc.ems.finance.agent.packet.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.packet.application.ScopeRecheckRepository;
import cn.com.crc.ems.finance.agent.packet.domain.ScopeRecheckRecord;
import cn.com.crc.ems.finance.agent.packet.infrastructure.entity.ScopeRecheckEntity;
import cn.com.crc.ems.finance.agent.packet.infrastructure.mapper.ScopeRecheckMapper;

/** 使用 MyBatis-Plus 持久化并查询发布前范围重查记录。 */
@Repository
public class MybatisScopeRecheckRepository implements ScopeRecheckRepository {

    private final ScopeRecheckMapper mapper;

    public MybatisScopeRecheckRepository(ScopeRecheckMapper mapper) {
        this.mapper = mapper;
    }

    /**
     *
     * @description: 保存一次发布前范围重查结果，保留决策摘要和审计字段。
     *
     * @param: record 范围重查领域记录
     * @author: yigeyige99
     */
    @Override
    public void insert(ScopeRecheckRecord record) {
        mapper.insert(MybatisPacketMappings.toEntity(record));
    }

    /**
     *
     * @description: 查询任务最近一次范围重查结果。
     *
     * @param: taskId 任务标识
     * @return: 最近一次重查记录，不存在时为空
     * @author: yigeyige99
     */
    @Override
    public Optional<ScopeRecheckRecord> findLatestByTaskId(UUID taskId) {
        return Optional.ofNullable(mapper.selectOne(new QueryWrapper<ScopeRecheckEntity>()
                .eq("task_id", taskId).orderByDesc("checked_at").last("LIMIT 1")))
                .map(MybatisPacketMappings::toDomain);
    }
}
