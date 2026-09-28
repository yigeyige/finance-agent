package cn.com.crc.ems.finance.agent.source.application;

import java.util.Map;

/** 合成源的固定目录和模板参数白名单。 */
public record SourceCatalog(String version, Map<String, Map<String, String>> templates) {

    public SourceCatalog {
        if (version == null || version.isBlank() || templates == null || templates.isEmpty()) {
            throw new IllegalArgumentException("source catalog is invalid");
        }
        templates = templates.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> Map.copyOf(entry.getValue())));
    }
}
