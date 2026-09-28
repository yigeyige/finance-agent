package cn.com.crc.ems.finance.agent.packet.application;

/** 发布前范围重查发现的变化类型。 */
public enum ScopeChangeType {
    ADDED,
    REMOVED,
    FIELDS_CHANGED,
    EFFECTIVE_CLASS_CHANGED,
    SOURCE_VERSION_CHANGED,
    RULE_VERSION_CHANGED,
    TEMPLATE_VERSION_CHANGED
}
