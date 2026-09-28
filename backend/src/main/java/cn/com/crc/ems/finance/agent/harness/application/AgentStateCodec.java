package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.harness.domain.StateReference;
import io.agentscope.core.state.State;

/** AgentScope State 与项目安全状态引用之间的序列化端口。 */
public interface AgentStateCodec {

    StateReference encode(State state);

    <T extends State> T decode(StateReference stateReference, Class<T> stateType);
}
