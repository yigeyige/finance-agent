package cn.com.crc.ems.finance.agent.source.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 合成源的固定目录和模板参数白名单。 */
public record SourceCatalog(String version, Map<String, Map<String, String>> templates,
        Map<String, List<String>> authorityOrder) {

    public SourceCatalog(String version, Map<String, Map<String, String>> templates) {
        this(version, templates, Map.of());
    }

    public SourceCatalog {
        if (version == null || version.isBlank() || templates == null || templates.isEmpty()
                || authorityOrder == null) {
            throw new IllegalArgumentException("source catalog is invalid");
        }
        templates = templates.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> Map.copyOf(entry.getValue())));
        authorityOrder = authorityOrder.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
        if (authorityOrder.entrySet().stream().anyMatch(entry -> entry.getKey() == null || entry.getKey().isBlank()
                || entry.getValue().isEmpty() || entry.getValue().stream().anyMatch(value -> value == null || value.isBlank()))) {
            throw new IllegalArgumentException("source authority catalog is invalid");
        }
    }

    /** 返回指定对象类型的来源权威顺序，高优先级来源在前。 */
    public List<String> authorityFor(String objectType) {
        return authorityOrder.getOrDefault(objectType, List.of());
    }
}
