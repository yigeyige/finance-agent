package cn.com.crc.ems.finance.agent.tool.application;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;
import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

/** 管理首期固定工具目录并校验 runtime bundle 版本绑定。 */
@Service
public class ToolCatalogService {

    private final RuntimeBundleRepository runtimeBundleRepository;
    private final ToolCatalog catalog;

    public ToolCatalogService(RuntimeBundleRepository runtimeBundleRepository) {
        this.runtimeBundleRepository = runtimeBundleRepository;
        this.catalog = fixedCatalog();
    }

    public ToolCatalog current() {
        return catalog;
    }

    /** 只允许已发布 runtime bundle 使用匹配版本和摘要的固定目录。 */
    public ToolDescriptor resolve(UUID runtimeBundleId, String toolCode, String descriptorVersion,
            TaskStatus taskStatus, String environment) {
        RuntimeBundle bundle = runtimeBundleRepository.findPublishedById(runtimeBundleId)
                .orElseThrow(() -> new IllegalArgumentException("runtime bundle is not published"));
        if (!catalog.version().equals(bundle.toolCatalogVersion()) || bundle.status() != RuntimeBundleStatus.PUBLISHED) {
            throw new IllegalArgumentException("runtime bundle tool catalog binding is stale");
        }
        ToolDescriptor descriptor = catalog.find(toolCode, descriptorVersion)
                .orElseThrow(() -> new IllegalArgumentException("tool is unknown or retired"));
        if (!descriptor.allowedStatuses().contains(taskStatus) || !descriptor.environmentAllowlist().contains(environment)) {
            throw new IllegalArgumentException("tool is not allowed in current state or environment");
        }
        return descriptor;
    }

    private static ToolCatalog fixedCatalog() {
        ToolDescriptor descriptor = new ToolDescriptor("read-config", "v1", "tool/read-config-input-v1",
                "tool/read-config-output-v1", "LOW", Set.of(TaskStatus.RUNNING_STEP, TaskStatus.ANALYZING), true,
                5_000, 1_000_000, Set.of("local", "test"), false);
        String canonical = descriptor.toolCode() + "|" + descriptor.version() + "|" + descriptor.inputSchemaRef()
                + "|" + descriptor.outputSchemaRef() + "|" + descriptor.riskLevel();
        return new ToolCatalog("tool-catalog-v1", Sha256Digest.hex(canonical),
                List.of(descriptor).stream().sorted(Comparator.comparing(ToolDescriptor::toolCode)).toList());
    }
}
