package cn.com.crc.ems.finance.agent.evidence.application;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;

/** 对分页枚举结果执行覆盖度契约校验。 */
@Service
public class CoverageValidator {

    /**
     * 
     * @description: 将枚举结果按 fail-closed 规则归类为完整、部分或未知覆盖
     *
     * @param: result 分页枚举原始证据
     * @return: 覆盖度枚举
     * @author: yigeyige99
     */
    public CoverageCompleteness validate(CoverageEnumerationResult<?> result) {
        if (result == null) {
            throw new IllegalArgumentException("coverage result is required");
        }
        if (result.pages().isEmpty() || result.expectedTotal() == null
                || result.failures().stream().anyMatch(failure -> failure.contains("changed")
                        || failure.contains("duplicate continuation token"))) {
            return CoverageCompleteness.UNKNOWN;
        }
        if (!result.failures().isEmpty() || !result.uncoveredPaths().isEmpty()
                || result.expectedTotal().longValue() != result.observedTotal()) {
            return CoverageCompleteness.PARTIAL;
        }
        return CoverageCompleteness.COMPLETE;
    }
}
