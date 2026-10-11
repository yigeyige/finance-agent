package cn.com.crc.ems.finance.agent.packet.application;

/**
 *
 * @Description: 发布前范围重查发现的变化类型。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum ScopeChangeType {
    ADDED,
    REMOVED,
    FIELDS_CHANGED,
    EFFECTIVE_CLASS_CHANGED,
    SOURCE_VERSION_CHANGED,
    RULE_VERSION_CHANGED,
    TEMPLATE_VERSION_CHANGED
}
