package cn.com.crc.ems.finance.agent.emsdbconfig.infrastructure.mapper;

import java.time.Instant;
import java.util.UUID;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 实例结构 JSONB 快照的 MyBatis 数据访问接口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
@Mapper
public interface EmsSchemaCatalogMapper {
    @Select("SELECT catalog_json::text FROM knowledge.ems_instance_schema_snapshot WHERE config_id = #{configId}")
    String findJson(@Param("configId") UUID configId);

    @Select("SELECT table_count FROM knowledge.ems_instance_schema_snapshot WHERE config_id = #{configId}")
    Integer findTableCount(@Param("configId") UUID configId);

    @Select("SELECT synced_at FROM knowledge.ems_instance_schema_snapshot WHERE config_id = #{configId}")
    Instant findSyncedAt(@Param("configId") UUID configId);

    @Insert("INSERT INTO knowledge.ems_instance_schema_snapshot(config_id, catalog_json, table_count, synced_at) "
            + "VALUES(#{configId}, CAST(#{json} AS jsonb), #{tableCount}, #{syncedAt}) "
            + "ON CONFLICT (config_id) DO UPDATE SET catalog_json = EXCLUDED.catalog_json, "
            + "table_count = EXCLUDED.table_count, synced_at = EXCLUDED.synced_at")
    void save(@Param("configId") UUID configId, @Param("json") String json,
            @Param("tableCount") int tableCount, @Param("syncedAt") Instant syncedAt);
}
