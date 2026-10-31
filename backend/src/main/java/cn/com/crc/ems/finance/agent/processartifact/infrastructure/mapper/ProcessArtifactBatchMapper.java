package cn.com.crc.ems.finance.agent.processartifact.infrastructure.mapper;

import java.util.UUID;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.processartifact.infrastructure.entity.ProcessArtifactBatchEntity;

/** 批次表映射和同任务串行保存锁。 */
@Mapper
public interface ProcessArtifactBatchMapper extends BaseMapper<ProcessArtifactBatchEntity> {
    @Select("SELECT task_id FROM task.investigation_task WHERE task_id = #{taskId} FOR UPDATE")
    UUID lockTask(UUID taskId);

    @Delete("DELETE FROM task.process_artifact_batch WHERE task_id = #{taskId} "
            + "AND batch_id NOT IN (SELECT batch_id FROM task.process_artifact_batch "
            + "WHERE task_id = #{taskId} ORDER BY created_at DESC, batch_id DESC LIMIT 2)")
    int deleteOlder(UUID taskId);
}
