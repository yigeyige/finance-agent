package cn.com.crc.ems.finance.agent.harness.application;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 不访问文件、网络、凭证或数据库的合成 Sandbox 计算样例。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 11:20:00
 */
public final class SyntheticSandboxComputation {

    /** 对受限整数输入执行稳定的合成计算。 */
    public Result calculate(int left, int right) {
        if (Math.abs((long) left) > 1_000_000 || Math.abs((long) right) > 1_000_000) {
            throw new IllegalArgumentException("synthetic input exceeds sandbox bound");
        }
        int sum = Math.addExact(left, right);
        return new Result(sum, Sha256Digest.hex(left + "|" + right + "|" + sum));
    }

    public record Result(int value, String digest) {
        public Result {
            if (digest == null || !digest.matches("[0-9a-f]{64}")) {
                throw new IllegalArgumentException("synthetic result digest is invalid");
            }
        }
    }
}
