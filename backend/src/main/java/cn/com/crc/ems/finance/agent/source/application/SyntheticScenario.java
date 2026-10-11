package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 合成源可重复切换的验证场景。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public enum SyntheticScenario {
    BASELINE,
    COMPLETE_NO_MATCH,
    PARTIAL_NO_MATCH,
    PAGINATION_FAILURE,
    CROSS_SOURCE_CONFLICT,
    FUTURE_REFERENCE,
    UNKNOWN_GAP,
    NEW_REFERENCE
}
