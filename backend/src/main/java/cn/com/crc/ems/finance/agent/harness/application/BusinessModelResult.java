package cn.com.crc.ems.finance.agent.harness.application;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Description: 模型业务产出、不含提示词和推理过程的调用元数据，以及输出规范化警告。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record BusinessModelResult<T>(T value, BusinessModelCallMetadata metadata, List<String> warnings) {

    public BusinessModelResult(T value, BusinessModelCallMetadata metadata) {
        this(value, metadata, List.of());
    }

    public BusinessModelResult {
        if (value == null || metadata == null) {
            throw new IllegalArgumentException("model value and metadata are required");
        }
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    /**
     *
     * @description: 追加一条输出规范化警告，空值和重复项会被忽略。
     *
     * @param: warning 待追加的安全警告文案
     * @return: 包含新警告的结果副本
     * @author: yigeyige99
     */
    public BusinessModelResult<T> withWarning(String warning) {
        if (warning == null || warning.isBlank() || warnings.contains(warning)) {
            return this;
        }
        List<String> merged = new ArrayList<>(warnings);
        merged.add(warning);
        return new BusinessModelResult<>(value, metadata, merged);
    }
}
