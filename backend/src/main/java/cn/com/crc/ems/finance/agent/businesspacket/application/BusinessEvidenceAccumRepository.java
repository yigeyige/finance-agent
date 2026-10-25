package cn.com.crc.ems.finance.agent.businesspacket.application;

import java.util.List;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.businesspacket.domain.BusinessEvidenceAccum;

/**
 *
 * @Description: 业务处理证据工作区持久化端口，按轮追加并读取累积证据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-24 10:00:00
 */
public interface BusinessEvidenceAccumRepository {

    /** 读取任务当前最大轮次，无任何记录时返回 0。 */
    int findMaxRoundNo(UUID taskId);

    /** 追加保存一轮证据，历史轮次保留。 */
    void save(BusinessEvidenceAccum accum);

    /** 按轮次升序读取任务全部累积证据。 */
    List<BusinessEvidenceAccum> findByTaskIdOrderByRound(UUID taskId);
}
