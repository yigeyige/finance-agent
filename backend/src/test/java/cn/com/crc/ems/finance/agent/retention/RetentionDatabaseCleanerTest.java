package cn.com.crc.ems.finance.agent.retention;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class RetentionDatabaseCleanerTest {

    @Test
    void repeatedBatchesAndCascadedRowsAppearInTableCounts() {
        FakeRepository repository = new FakeRepository();
        repository.counts.put("task.investigation_task", 503L);
        repository.counts.put("task.task_attachment", 2L);
        repository.remainingTasks = 503;
        RetentionDatabaseCleaner cleaner = new RetentionDatabaseCleaner(repository, 500);

        RetentionDatabaseCleaner.Result result = cleaner.clean(Instant.parse("2026-10-05T00:00:00Z"));

        assertThat(result.deleted()).containsEntry("task.investigation_task", 503L)
                .containsEntry("task.task_attachment", 2L);
        assertThat(result.errors()).isEmpty();
        assertThat(repository.taskCalls).isEqualTo(2);
    }

    private static final class FakeRepository implements RetentionDatabaseCleaner.Repository {
        private final Map<String, Long> counts = new HashMap<>();
        private int remainingTasks;
        private int taskCalls;

        @Override
        public Map<String, Long> countAll() { return new HashMap<>(counts); }

        @Override
        public int deleteBatch(RetentionCatalog.Table table, Instant cutoff, int limit) {
            if (table.fullName().equals("task.investigation_task")) {
                taskCalls++;
                int removed = Math.min(limit, remainingTasks);
                remainingTasks -= removed;
                counts.put("task.investigation_task", (long) remainingTasks);
                if (removed > 0) counts.put("task.task_attachment", 0L);
                return removed;
            }
            return 0;
        }
    }
}
