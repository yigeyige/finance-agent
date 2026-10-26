package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsTableKnowledge;

/**
 *
 * @Description: EMS 表知识的持久化端口，本次仅提供按连接实例读取；导入走一次性 SQL。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-25 00:00:00
 */
public interface EmsTableKnowledgeRepository {
    List<EmsTableKnowledge> findByConfigId(UUID configId);
}
