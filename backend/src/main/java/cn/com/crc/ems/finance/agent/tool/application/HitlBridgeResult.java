package cn.com.crc.ems.finance.agent.tool.application;

/** HITL bridge 的脱敏结果；permit 只在服务端内存中交给 ToolGateway。 */
public record HitlBridgeResult(HitlEvent event, SignedToolPermit permit) {

    public HitlBridgeResult {
        if (event == null || event.type() == HitlEventType.APPROVED && permit == null
                || event.type() != HitlEventType.APPROVED && permit != null) {
            throw new IllegalArgumentException("HITL bridge result is invalid");
        }
    }
}
