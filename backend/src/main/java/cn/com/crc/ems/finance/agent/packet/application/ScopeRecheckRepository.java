package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.ScopeRecheckRecord;

/** 发布前范围重查记录仓储端口。 */
public interface ScopeRecheckRepository {

    void insert(ScopeRecheckRecord record);

    Optional<ScopeRecheckRecord> findLatestByTaskId(UUID taskId);
}
