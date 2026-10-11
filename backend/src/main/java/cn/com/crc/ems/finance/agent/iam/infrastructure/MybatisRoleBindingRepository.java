package cn.com.crc.ems.finance.agent.iam.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import cn.com.crc.ems.finance.agent.iam.application.RoleBindingRepository;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.RoleBindingEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.mapper.RoleBindingMapper;

/**
 *
 * @Description: 使用 MyBatis-Plus Mapper 访问可撤销的角色授权关系。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Repository
public class MybatisRoleBindingRepository implements RoleBindingRepository {

    private final RoleBindingMapper mapper;

    public MybatisRoleBindingRepository(RoleBindingMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<RoleBinding> findByUserId(UUID userId) {
        return mapper.selectList(new QueryWrapper<RoleBindingEntity>().eq("user_id", userId).orderByAsc("granted_at"))
                .stream().map(MybatisIamMappings::toDomain).toList();
    }

    @Override
    public void grant(RoleBinding binding) {
        RoleBindingEntity active = mapper.selectOne(new QueryWrapper<RoleBindingEntity>()
                .eq("user_id", binding.userId()).eq("role_code", binding.roleCode().name()).isNull("revoked_at"));
        if (active == null) {
            mapper.insert(MybatisIamMappings.toEntity(binding));
        }
    }

    @Override
    public void revoke(UUID userId, RoleCode roleCode, Instant revokedAt, UUID actorId) {
        mapper.update(null, new UpdateWrapper<RoleBindingEntity>().eq("user_id", userId)
                .eq("role_code", roleCode.name()).isNull("revoked_at")
                .set("revoked_at", revokedAt).set("updated_by", actorId).set("updated_at", revokedAt));
    }
}
