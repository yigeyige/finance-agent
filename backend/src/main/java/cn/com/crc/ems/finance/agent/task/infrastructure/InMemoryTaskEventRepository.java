package cn.com.crc.ems.finance.agent.task.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import cn.com.crc.ems.finance.agent.task.application.PublicTaskEvent;
import cn.com.crc.ems.finance.agent.task.application.TaskEventRepository;

/** 可替换的进程内事件仓储，供本地契约和后续持久化事件表适配。 */
@Repository
public class InMemoryTaskEventRepository implements TaskEventRepository {

    private final ConcurrentHashMap<UUID, AtomicLong> sequences = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, CopyOnWriteArrayList<PublicTaskEvent>> events = new ConcurrentHashMap<>();

    @Override
    public PublicTaskEvent append(UUID taskId, UUID userId, String eventType, String status, String publicReason,
            String resultReference, Instant occurredAt) {
        if (taskId == null || userId == null || occurredAt == null) {
            throw new IllegalArgumentException("taskId, userId and occurredAt are required");
        }
        long sequence = sequences.computeIfAbsent(taskId, ignored -> new AtomicLong()).incrementAndGet();
        PublicTaskEvent event = new PublicTaskEvent(taskId, userId, sequence, eventType, status, publicReason,
                resultReference, occurredAt);
        events.computeIfAbsent(taskId, ignored -> new CopyOnWriteArrayList<>()).add(event);
        return event;
    }

    @Override
    public List<PublicTaskEvent> findAfter(UUID taskId, UUID userId, long afterSequence, int limit) {
        if (taskId == null || userId == null || afterSequence < 0 || limit < 1 || limit > 500) {
            throw new IllegalArgumentException("task, user and bounded non-negative cursor are required");
        }
        List<PublicTaskEvent> result = new ArrayList<>();
        for (PublicTaskEvent event : events.getOrDefault(taskId, new CopyOnWriteArrayList<>())) {
            if (event.userId().equals(userId) && event.sequence() > afterSequence) {
                result.add(event);
            }
        }
        result.sort(Comparator.comparingLong(PublicTaskEvent::sequence));
        return result.size() <= limit ? List.copyOf(result) : List.copyOf(result.subList(0, limit));
    }
}
