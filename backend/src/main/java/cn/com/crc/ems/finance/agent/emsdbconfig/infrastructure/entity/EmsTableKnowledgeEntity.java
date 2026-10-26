package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 *
 * @Description: EMS 表知识的持久化实体，knowledge 列为 JSONB 文本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 00:00:00
 */
@TableName("governance.ems_table_knowledge")
public class EmsTableKnowledgeEntity {
    public String configId;
    public String schemaName;
    public String tableName;
    public String tableComment;
    public String dataStatus;
    public String knowledge;
    public UUID createdBy;
    public Instant createdAt;
    public UUID updatedBy;
    public Instant updatedAt;
}
