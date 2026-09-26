package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.com.crc.ems.finance.agent.task.application.StepApprovalRepository;
import cn.com.crc.ems.finance.agent.task.domain.StepApproval;
import cn.com.crc.ems.finance.agent.task.infrastructure.entity.StepApprovalEntity;
import cn.com.crc.ems.finance.agent.task.infrastructure.mapper.StepApprovalMapper;

/** 使用 MyBatis-Plus Mapper 访问步骤审批历史。 */
@Repository
public class MybatisStepApprovalRepository implements StepApprovalRepository {

    private final StepApprovalMapper mapper;

    public MybatisStepApprovalRepository(StepApprovalMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<StepApproval> findByStepId(UUID stepId) {
        return mapper.selectList(new QueryWrapper<StepApprovalEntity>().eq("step_id", stepId)
                        .orderByAsc("revision_no"))
                .stream().map(MybatisTaskMappings::toDomain).toList();
    }

    @Override
    public void insert(StepApproval approval) {
        mapper.insert(MybatisTaskMappings.toEntity(approval));
    }
}
