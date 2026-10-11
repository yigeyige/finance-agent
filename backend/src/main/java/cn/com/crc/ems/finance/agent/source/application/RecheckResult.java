package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 统一来源 SPI 的复查结果契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record RecheckResult(RecheckStatus status, String code, String resultDigest) {

    public RecheckResult {
        if (status == null || code == null || code.isBlank() || resultDigest == null || resultDigest.isBlank()) {
            throw new IllegalArgumentException("recheck result is invalid");
        }
    }
}
