package cn.com.crc.ems.finance.agent.retention;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @Description: 按固定表白名单分批清理到期运行记录，并以各表实际行数差计算级联删除量。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
final class RetentionDatabaseCleaner {
    interface Repository {
        Map<String, Long> countAll();
        int deleteBatch(RetentionCatalog.Table table, Instant cutoff, int limit);
    }

    record Result(Map<String, Long> deleted, List<String> errors) { }

    private final Repository repository;
    private final int batchSize;

    RetentionDatabaseCleaner(Repository repository, int batchSize) {
        this.repository = repository;
        this.batchSize = batchSize;
    }

    /**
     * @description: 每表循环删除固定数量的到期行，单批失败时记录表名并继续其他表。
     * @param: cutoff 本次运行固定的到期截止时间。
     * @return: 按表统计的实际行数差及失败表名。
     * @author: yigeyige99
     */
    Result clean(Instant cutoff) {
        Map<String, Long> before = repository.countAll();
        List<String> errors = new ArrayList<>();
        for (RetentionCatalog.Table table : RetentionCatalog.tables()) {
            try {
                int deleted;
                do {
                    deleted = repository.deleteBatch(table, cutoff, batchSize);
                } while (deleted == batchSize);
            } catch (RuntimeException exception) {
                errors.add(table.fullName() + ":" + exception.getClass().getSimpleName());
            }
        }
        Map<String, Long> after = repository.countAll();
        Map<String, Long> deleted = new LinkedHashMap<>();
        for (RetentionCatalog.Table table : RetentionCatalog.tables()) {
            String name = table.fullName();
            deleted.put(name, Math.max(0L, before.getOrDefault(name, 0L) - after.getOrDefault(name, 0L)));
        }
        return new Result(Map.copyOf(deleted), List.copyOf(errors));
    }
}
