package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsTableKnowledgeRepository;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsTableDataStatus;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsTableKnowledge;
import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.entity.EmsTableKnowledgeEntity;
import cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.mapper.EmsTableKnowledgeMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.PersistenceUuid;

/**
 *
 * @Description: 使用 MyBatis-Plus 读取 EMS 表知识。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 00:00:00
 */
@Repository
public class MybatisEmsTableKnowledgeRepository implements EmsTableKnowledgeRepository {
    private final EmsTableKnowledgeMapper mapper;

    public MybatisEmsTableKnowledgeRepository(EmsTableKnowledgeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<EmsTableKnowledge> findByConfigId(UUID configId) {
        return mapper.selectList(new QueryWrapper<EmsTableKnowledgeEntity>().eq("config_id", PersistenceUuid.toStorage(configId)))
                .stream().map(MybatisEmsTableKnowledgeRepository::toDomain).toList();
    }

    private static EmsTableKnowledge toDomain(EmsTableKnowledgeEntity value) {
        return new EmsTableKnowledge(PersistenceUuid.toDomain(value.configId), value.schemaName, value.tableName,
                value.tableComment, EmsTableDataStatus.valueOf(value.dataStatus), value.knowledge);
    }
}
