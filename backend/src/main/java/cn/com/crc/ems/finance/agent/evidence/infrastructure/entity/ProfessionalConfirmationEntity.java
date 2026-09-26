package cn.com.crc.ems.finance.agent.evidence.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import cn.com.crc.ems.finance.agent.task.infrastructure.JsonbTypeHandler;

/** 专业确认表的 MyBatis-Plus 持久化实体。 */
@TableName("evidence.professional_confirmation")
public class ProfessionalConfirmationEntity {
    @TableId(value = "confirmation_id", type = IdType.ASSIGN_UUID)
    public String confirmationId;
    public UUID findingId;
    public UUID confirmedBy;
    public Instant confirmedAt;
    public UUID scopeRevisionId;
    public String decision;
    public String rationale;
    @TableField(value = "evidence_refs", typeHandler = JsonbTypeHandler.class)
    public String evidenceRefs;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
