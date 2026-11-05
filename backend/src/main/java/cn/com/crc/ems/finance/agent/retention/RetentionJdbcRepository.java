package cn.com.crc.ems.finance.agent.retention;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 使用固定 SQL 白名单执行跨 31 张表的有界清理；批量删除与事务内审计标志属于 PostgreSQL 特殊能力。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
@Repository
class RetentionJdbcRepository implements RetentionDatabaseCleaner.Repository {
    private final JdbcTemplate jdbc;

    RetentionJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * @description: 获取清理前后各表行数，以包含外键级联产生的实际删除量。
     * @return: 白名单中各表的行数。
     * @author: yigeyige99
     */
    @Override
    public Map<String, Long> countAll() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (RetentionCatalog.Table table : RetentionCatalog.tables()) {
            counts.put(table.fullName(), jdbc.queryForObject(
                    "SELECT COUNT(*) FROM " + table.fullName(), Long.class));
        }
        return counts;
    }

    /**
     * @description: 在独立事务内删除一批到期记录；审计表只在该事务内开启删除标志。
     * @param: table 固定清理表。
     * @param: cutoff 截止时间。
     * @param: limit 单批最大主记录数。
     * @return: 直接删除的主记录数，不含级联行。
     * @author: yigeyige99
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteBatch(RetentionCatalog.Table table, Instant cutoff, int limit) {
        if (table.fullName().equals("governance.audit_event")) {
            jdbc.queryForObject("SELECT set_config('finance_agent.retention_cleanup', 'on', true)", String.class);
        }
        Object[] arguments = new Object[table.cutoffParameters() + 1];
        for (int index = 0; index < table.cutoffParameters(); index++) {
            arguments[index] = Timestamp.from(cutoff);
        }
        arguments[arguments.length - 1] = limit;
        return jdbc.update(table.deleteSql(), arguments);
    }
}
