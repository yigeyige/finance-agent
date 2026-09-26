package cn.com.crc.ems.finance.agent.iam.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.iam.domain.RoleBinding;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/** 角色授权关系持久化端口。 */
public interface RoleBindingRepository {

    /** 查询身份的全部授权历史。 */
    List<RoleBinding> findByUserId(UUID userId);

    /** 授予角色，已有活动授权时保持幂等。 */
    void grant(RoleBinding binding);

    /** 撤销活动角色并写入撤销审计人。 */
    void revoke(UUID userId, RoleCode roleCode, Instant revokedAt, UUID actorId);
}
