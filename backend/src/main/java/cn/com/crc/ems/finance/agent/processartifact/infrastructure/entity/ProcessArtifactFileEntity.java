package cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 单文件正文持久化映射。 */
@TableName("task.process_artifact_file")
public class ProcessArtifactFileEntity {
    @TableId(value = "file_id", type = IdType.INPUT)
    public String fileId;
    public UUID batchId;
    public String kind;
    public String fileName;
    public String contentText;
}
