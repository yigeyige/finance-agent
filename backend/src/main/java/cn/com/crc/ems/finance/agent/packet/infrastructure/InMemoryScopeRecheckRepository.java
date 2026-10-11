package cn.com.crc.ems.finance.agent.packet.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cn.com.crc.ems.finance.agent.packet.application.ScopeRecheckRepository;
import cn.com.crc.ems.finance.agent.packet.domain.ScopeRecheckRecord;

/**
 *
 * @Description: 本地开发阶段使用的进程内重查记录仓储。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public class InMemoryScopeRecheckRepository implements ScopeRecheckRepository {

    private final ConcurrentMap<UUID, List<ScopeRecheckRecord>> records = new ConcurrentHashMap<>();

    @Override
    public void insert(ScopeRecheckRecord record) {
        records.compute(record.taskId(), (taskId, current) -> {
            List<ScopeRecheckRecord> values = current == null ? new ArrayList<>() : new ArrayList<>(current);
            values.add(record);
            return List.copyOf(values);
        });
    }

    @Override
    public Optional<ScopeRecheckRecord> findLatestByTaskId(UUID taskId) {
        List<ScopeRecheckRecord> values = records.get(taskId);
        return values == null ? Optional.empty() : values.stream().reduce((first, second) -> second);
    }
}
