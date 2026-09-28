package cn.com.crc.ems.finance.agent.tool.application;

import java.util.List;
import java.util.Optional;

/** 按版本和摘要冻结的工具目录。 */
public record ToolCatalog(String version, String digest, List<ToolDescriptor> tools) {

    public ToolCatalog {
        if (version == null || version.isBlank() || digest == null || !digest.matches("[0-9a-f]{64}")
                || tools == null || tools.isEmpty()) {
            throw new IllegalArgumentException("tool catalog is invalid");
        }
        tools = List.copyOf(tools);
    }

    public Optional<ToolDescriptor> find(String toolCode, String descriptorVersion) {
        return tools.stream().filter(tool -> tool.toolCode().equals(toolCode)
                && tool.version().equals(descriptorVersion) && !tool.retired()).findFirst();
    }
}
