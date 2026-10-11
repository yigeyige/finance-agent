package cn.com.crc.ems.finance.agent.governance.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: 运行版本表的 MyBatis-Plus 持久化实体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@TableName("governance.runtime_bundle")
public class RuntimeBundleEntity {
    @TableId(value = "runtime_bundle_id", type = IdType.ASSIGN_UUID)
    public String runtimeBundleId;
    public String applicationVersion;
    public String agentscopeVersion;
    public String modelBindingVersion;
    public String promptBundleVersion;
    public String ruleSetVersion;
    public String toolCatalogVersion;
    public String sourceCatalogVersion;
    public UUID knowledgeReleaseId;
    public String bundleDigest;
    public String status;
    public Instant createdAt;
    public UUID createdBy;
    public Instant updatedAt;
    public UUID updatedBy;
}
