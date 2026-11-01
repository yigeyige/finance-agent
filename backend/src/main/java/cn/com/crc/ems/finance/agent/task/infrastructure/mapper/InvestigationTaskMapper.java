package cn.com.crc.ems.finance.agent.task.infrastructure.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.task.infrastructure.entity.InvestigationTaskEntity;

/**
 *
 * @Description: 调查任务表的 MyBatis-Plus Mapper，列表查询显式绑定筛选、计数和分页参数。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-01 00:00:00
 */
@Mapper
public interface InvestigationTaskMapper extends BaseMapper<InvestigationTaskEntity> {

    String PAGE_FILTER = "<where>"
            + "<if test='initiatorUserId != null'> AND initiator_user_id = #{initiatorUserId} </if>"
            + "<if test='status != null'> AND status = #{status} </if>"
            + "<if test='keyword != null'> AND title LIKE CONCAT('%', #{keyword}, '%') </if>"
            + "</where>";

    /**
     * @description: 使用与分页记录相同的身份及筛选条件统计任务总数。
     * @param: initiatorUserId 普通操作人的身份范围，管理员查询时为空
     * @param: status 可选任务状态
     * @param: keyword 可选标题关键字
     * @return: 筛选范围内全部任务数
     * @author: yigeyige99
     */
    @Select({ "<script>", "SELECT COUNT(*) FROM task.investigation_task", PAGE_FILTER, "</script>" })
    long countPageRows(@Param("initiatorUserId") UUID initiatorUserId, @Param("status") String status,
            @Param("keyword") String keyword);

    /**
     * @description: 按稳定倒序和绑定的行数、偏移量读取单页任务。
     * @param: initiatorUserId 普通操作人的身份范围，管理员查询时为空
     * @param: status 可选任务状态
     * @param: keyword 可选标题关键字
     * @param: limit 单页行数上限
     * @param: offset 从零开始的记录偏移量
     * @return: 当前页的任务持久化实体
     * @author: yigeyige99
     */
    @Select({ "<script>", "SELECT * FROM task.investigation_task", PAGE_FILTER,
            "ORDER BY created_at DESC, task_id DESC LIMIT #{limit} OFFSET #{offset}", "</script>" })
    List<InvestigationTaskEntity> selectPageRows(@Param("initiatorUserId") UUID initiatorUserId,
            @Param("status") String status, @Param("keyword") String keyword, @Param("limit") int limit,
            @Param("offset") long offset);

    /** 在当前写事务中锁定任务行，供跨仓储用例建立稳定锁顺序。 */
    @Select("SELECT * FROM task.investigation_task "
            + "WHERE task_id = CAST(#{taskId} AS uuid) FOR UPDATE")
    InvestigationTaskEntity selectByIdForUpdate(@Param("taskId") String taskId);
}
