package cn.com.crc.ems.finance.agent.evidence.application;

/**
 *
 * @Description: 表达两个固定测试来源交叉印证后的可审计结果及所使用的目录版本。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public record TestSourceCrossValidationResult(String catalogVersion, SourceConflictResult conflict) {

    public TestSourceCrossValidationResult {
        if (catalogVersion == null || catalogVersion.isBlank() || conflict == null) {
            throw new IllegalArgumentException("test source cross-validation result is invalid");
        }
    }
}
