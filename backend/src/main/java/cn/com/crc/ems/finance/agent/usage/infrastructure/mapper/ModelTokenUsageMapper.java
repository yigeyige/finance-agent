package cn.com.crc.ems.finance.agent.usage.infrastructure.mapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.com.crc.ems.finance.agent.usage.infrastructure.entity.ModelTokenUsageEntity;

/**
 * @Description: 模型 token 用量表 Mapper：写入单条记录并按业务时区做按天聚合。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 14:10:00
 */
@Mapper
public interface ModelTokenUsageMapper extends BaseMapper<ModelTokenUsageEntity> {

    /** 聚合全平台最近区间的每日 token 用量。 */
    @Select("""
            SELECT (occurred_at AT TIME ZONE 'Asia/Shanghai')::date AS usage_date,
                   COALESCE(SUM(tokens), 0) AS total_tokens
              FROM usage.model_token_usage
             WHERE mode = 'LIVE'
               AND occurred_at >= #{from}
               AND occurred_at < #{to}
             GROUP BY 1
            """)
    List<ModelTokenUsageDailyRow> sumDailyAll(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    /** 聚合指定用户在最近区间的每日 token 用量。 */
    @Select("""
            SELECT (occurred_at AT TIME ZONE 'Asia/Shanghai')::date AS usage_date,
                   COALESCE(SUM(tokens), 0) AS total_tokens
              FROM usage.model_token_usage
             WHERE mode = 'LIVE'
               AND user_id = #{userId}
               AND occurred_at >= #{from}
               AND occurred_at < #{to}
             GROUP BY 1
            """)
    List<ModelTokenUsageDailyRow> sumDailyByUser(@Param("userId") UUID userId,
            @Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}
