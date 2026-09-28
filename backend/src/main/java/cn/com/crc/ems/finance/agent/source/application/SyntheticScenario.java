package cn.com.crc.ems.finance.agent.source.application;

/** 合成源可重复切换的验证场景。 */
public enum SyntheticScenario {
    BASELINE,
    PAGINATION_FAILURE,
    CROSS_SOURCE_CONFLICT,
    FUTURE_REFERENCE,
    UNKNOWN_GAP,
    NEW_REFERENCE
}
