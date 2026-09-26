package cn.com.crc.ems.finance.agent.iam.infrastructure;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;
import cn.com.crc.ems.finance.agent.iam.domain.AgentUserStatus;
import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.AgentUserEntity;
import cn.com.crc.ems.finance.agent.iam.infrastructure.entity.RoleBindingEntity;

/** IAM 持久化实体与领域快照之间的显式映射。 */
final class MybatisIamMappings {

    private MybatisIamMappings() {
    }

    static AgentUser toDomain(AgentUserEntity entity) {
        return new AgentUser(entity.userId, entity.authSubject, entity.employeeNo, entity.displayName,
                AgentUserStatus.valueOf(entity.status), entity.createdAt, entity.createdBy, entity.updatedAt, entity.updatedBy);
    }

    static AgentUserEntity toEntity(AgentUser user) {
        AgentUserEntity entity = new AgentUserEntity();
        entity.userId = user.userId();
        entity.authSubject = user.authSubject();
        entity.employeeNo = user.employeeNo();
        entity.displayName = user.displayName();
        entity.status = user.status().name();
        entity.createdAt = user.createdAt();
        entity.createdBy = user.createdBy();
        entity.updatedAt = user.updatedAt();
        entity.updatedBy = user.updatedBy();
        return entity;
    }

    static RoleBinding toDomain(RoleBindingEntity entity) {
        return new RoleBinding(entity.userId, RoleCode.valueOf(entity.roleCode), entity.grantedBy, entity.grantedAt,
                entity.revokedAt, entity.createdBy, entity.createdAt, entity.updatedBy, entity.updatedAt);
    }

    static RoleBindingEntity toEntity(RoleBinding binding) {
        RoleBindingEntity entity = new RoleBindingEntity();
        entity.userId = binding.userId();
        entity.roleCode = binding.roleCode().name();
        entity.grantedBy = binding.grantedBy();
        entity.grantedAt = binding.grantedAt();
        entity.revokedAt = binding.revokedAt();
        entity.createdBy = binding.createdBy();
        entity.createdAt = binding.createdAt();
        entity.updatedBy = binding.updatedBy();
        entity.updatedAt = binding.updatedAt();
        return entity;
    }
}
