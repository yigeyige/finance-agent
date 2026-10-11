package cn.com.crc.ems.finance.agent.iam.application;

import java.util.Optional;
import java.util.UUID;

import cn.com.crc.ems.finance.agent.iam.domain.AgentUser;

/**
 *
 * @Description: 身份账户持久化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface AgentUserRepository {

    /** 按主键查询身份。 */
    Optional<AgentUser> findById(UUID userId);

    /** 按认证主体查询身份。 */
    Optional<AgentUser> findByAuthSubject(String authSubject);

    /** 按人员编号查询身份。 */
    Optional<AgentUser> findByEmployeeNo(String employeeNo);

    /** 更新已有身份及其审计字段。 */
    void save(AgentUser user);

    /** 幂等插入合成或初始化身份。 */
    void insertIfAbsent(AgentUser user);
}
