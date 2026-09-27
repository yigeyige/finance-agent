package cn.com.crc.ems.finance.agent.task.application;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.shared.infrastructure.PostgresRepositoryExceptionMapper;
import cn.com.crc.ems.finance.agent.shared.infrastructure.RepositoryEntityNotFoundException;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStateConflictException;
import cn.com.crc.ems.finance.agent.task.domain.InvocationStatus;
import cn.com.crc.ems.finance.agent.task.domain.ToolInvocation;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/** 管理工具调用的幂等意图、条件状态转换和结果分类。 */
@Service
public class ToolInvocationService {

    private final ToolInvocationRepository repository;

    public ToolInvocationService(ToolInvocationRepository repository) {
        this.repository = repository;
    }

    /** 在任何外部工具副作用发生前记录意图；重复请求只返回原记录。 */
    @Transactional
    public ToolInvocationStartResult startIntent(ToolInvocationStartCommand command) {
        ToolInvocationStartResult existing = repository.findByIdempotencyKey(command.key().value())
                .map(this::classifyExisting).orElse(null);
        if (existing != null) {
            return existing;
        }

        Instant now = command.requestedAt();
        ToolInvocation invocation = new ToolInvocation(UuidGenerator.newUuid(), command.key().taskId(),
                command.key().stepId(), command.approvalId(), command.toolCode(), command.key().value(),
                command.requestDigest().toLowerCase(), null, InvocationStatus.INTENT_RECORDED, now, null,
                null, command.actorId(), now, command.actorId(), now);
        try {
            repository.insert(invocation);
            return new ToolInvocationStartResult(ToolInvocationStartResult.Action.EXECUTE, invocation);
        } catch (DataIntegrityViolationException failure) {
            Optional<ToolInvocation> raced = repository.findByIdempotencyKey(command.key().value());
            if (raced.isPresent()) {
                return classifyExisting(raced.get());
            }
            throw PostgresRepositoryExceptionMapper.map("tool invocation intent", failure);
        }
    }

    /** 仅允许将尚未运行的意图推进为运行中。 */
    @Transactional
    public ToolInvocation markRunning(String idempotencyKey, UUID actorId, Instant occurredAt) {
        requireKeyAndTime(idempotencyKey, actorId, occurredAt);
        ToolInvocation current = find(idempotencyKey);
        if (current.status() == InvocationStatus.RUNNING) {
            return current;
        }
        if (current.status() != InvocationStatus.INTENT_RECORDED) {
            throw new InvocationStateConflictException("invocation is not awaiting execution");
        }
        if (repository.updateStatusIfCurrent(idempotencyKey, InvocationStatus.INTENT_RECORDED,
                InvocationStatus.RUNNING, null, null, null, occurredAt, actorId)) {
            return find(idempotencyKey);
        }
        ToolInvocation afterRace = find(idempotencyKey);
        if (afterRace.status() == InvocationStatus.RUNNING) {
            return afterRace;
        }
        throw new InvocationStateConflictException("invocation state changed concurrently");
    }

    /** 持久化确定结果或未知结果；终态不可被其他调用覆盖。 */
    @Transactional
    public ToolInvocation complete(String idempotencyKey, InvocationStatus targetStatus, String responseDigest,
            String errorCode, UUID actorId, Instant completedAt) {
        requireKeyAndTime(idempotencyKey, actorId, completedAt);
        if (targetStatus != InvocationStatus.SUCCEEDED && targetStatus != InvocationStatus.PARTIAL
                && targetStatus != InvocationStatus.FAILED && targetStatus != InvocationStatus.UNKNOWN) {
            throw new IllegalArgumentException("completion requires a terminal invocation status");
        }
        if (responseDigest != null && !responseDigest.matches("^[0-9a-fA-F]{64}$")) {
            throw new IllegalArgumentException("responseDigest must be a SHA-256 hex digest");
        }
        if ((targetStatus == InvocationStatus.FAILED || targetStatus == InvocationStatus.UNKNOWN)
                && (errorCode == null || errorCode.isBlank())) {
            throw new IllegalArgumentException("failure and unknown results require an errorCode");
        }

        ToolInvocation current = find(idempotencyKey);
        if (isTerminal(current.status())) {
            if (current.status() == targetStatus) {
                return current;
            }
            throw new InvocationStateConflictException("terminal invocation cannot be overwritten");
        }
        if (current.status() != InvocationStatus.INTENT_RECORDED && current.status() != InvocationStatus.RUNNING) {
            throw new InvocationStateConflictException("invocation is not completable");
        }
        if (repository.updateStatusIfCurrent(idempotencyKey, current.status(), targetStatus, responseDigest,
                errorCode, completedAt, completedAt, actorId)) {
            return find(idempotencyKey);
        }
        ToolInvocation afterRace = find(idempotencyKey);
        if (afterRace.status() == targetStatus) {
            return afterRace;
        }
        throw new InvocationStateConflictException("invocation state changed concurrently");
    }

    private ToolInvocationStartResult classifyExisting(ToolInvocation invocation) {
        return switch (invocation.status()) {
            case SUCCEEDED, PARTIAL, FAILED -> new ToolInvocationStartResult(ToolInvocationStartResult.Action.REPLAY,
                    invocation);
            case UNKNOWN -> new ToolInvocationStartResult(ToolInvocationStartResult.Action.MANUAL_RECONCILIATION,
                    invocation);
            case INTENT_RECORDED, RUNNING -> new ToolInvocationStartResult(ToolInvocationStartResult.Action.IN_PROGRESS,
                    invocation);
        };
    }

    private ToolInvocation find(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new RepositoryEntityNotFoundException("tool invocation not found"));
    }

    private static boolean isTerminal(InvocationStatus status) {
        return status == InvocationStatus.SUCCEEDED || status == InvocationStatus.PARTIAL
                || status == InvocationStatus.FAILED || status == InvocationStatus.UNKNOWN;
    }

    private static void requireKeyAndTime(String idempotencyKey, UUID actorId, Instant occurredAt) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || actorId == null || occurredAt == null) {
            throw new IllegalArgumentException("idempotencyKey, actorId and timestamp are required");
        }
    }
}
