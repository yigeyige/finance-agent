package cn.com.crc.ems.finance.agent.packet.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.packet.domain.ScopeRecheckRecord;

/**
 *
 * @Description: 发布前范围重查记录仓储端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface ScopeRecheckRepository {

    void insert(ScopeRecheckRecord record);

    Optional<ScopeRecheckRecord> findLatestByTaskId(UUID taskId);
}
