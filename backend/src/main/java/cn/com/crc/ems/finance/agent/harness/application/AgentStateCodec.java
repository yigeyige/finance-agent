package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import io.agentscope.core.state.State;

/**
 *
 * @Description: AgentScope State 与项目安全状态引用之间的序列化端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface AgentStateCodec {

    StateReference encode(State state);

    <T extends State> T decode(StateReference stateReference, Class<T> stateType);
}
