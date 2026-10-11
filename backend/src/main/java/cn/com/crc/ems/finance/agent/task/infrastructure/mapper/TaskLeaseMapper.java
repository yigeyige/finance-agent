package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import java.time.Instant;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.TaskLeaseEntity;

/**
 *
 * @Description: 任务租约表的 MyBatis-Plus Mapper。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Mapper
public interface TaskLeaseMapper extends BaseMapper<TaskLeaseEntity> {

    /** 查询严格过期的未释放租约，稳定按过期时间分页。 */
    @Select("SELECT * FROM task.task_lease WHERE released_at IS NULL "
            + "AND expires_at < #{observedAt} ORDER BY expires_at ASC, lease_id ASC LIMIT #{limit}")
    List<TaskLeaseEntity> selectExpired(@Param("observedAt") Instant observedAt, @Param("limit") int limit);
}
