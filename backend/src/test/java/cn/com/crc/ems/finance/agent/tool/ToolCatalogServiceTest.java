package cn.com.crc.ems.finance.agent.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.governance.application.RuntimeBundleRepository;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundle;
import cn.com.crc.ems.finance.agent.governance.domain.RuntimeBundleStatus;
import cn.com.crc.ems.finance.agent.tool.application.ToolCatalogService;
import cn.com.crc.ems.finance.agent.task.domain.TaskStatus;

class ToolCatalogServiceTest {

    @Test
    void resolvesOnlyFixedCatalogBoundToPublishedBundle() {
        RuntimeBundleRepository bundles = mock(RuntimeBundleRepository.class);
        ToolCatalogService service = new ToolCatalogService(bundles);
        var catalog = service.current();
        UUID id = UUID.randomUUID();
        RuntimeBundle bundle = new RuntimeBundle(id, "local", "2.0.3", "model-v1", "prompt-v1", "rules-v1",
                catalog.version(), "source-v1", UUID.randomUUID(), catalog.digest(), RuntimeBundleStatus.PUBLISHED,
                Instant.now(), UUID.randomUUID(), Instant.now(), UUID.randomUUID());
        when(bundles.findPublishedById(id)).thenReturn(Optional.of(bundle));

        assertThat(service.resolve(id, "read-config", "v1", TaskStatus.RUNNING_STEP, "local").toolCode())
                .isEqualTo("read-config");
    }

    @Test
    void rejectsUnknownToolOrEnvironment() {
        RuntimeBundleRepository bundles = mock(RuntimeBundleRepository.class);
        ToolCatalogService service = new ToolCatalogService(bundles);
        UUID id = UUID.randomUUID();
        var catalog = service.current();
        RuntimeBundle bundle = new RuntimeBundle(id, "local", "2.0.3", "model-v1", "prompt-v1", "rules-v1",
                catalog.version(), "source-v1", UUID.randomUUID(), catalog.digest(), RuntimeBundleStatus.PUBLISHED,
                Instant.now(), UUID.randomUUID(), Instant.now(), UUID.randomUUID());
        when(bundles.findPublishedById(id)).thenReturn(Optional.of(bundle));

        assertThatThrownBy(() -> service.resolve(id, "unknown", "v1", TaskStatus.RUNNING_STEP, "local"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.resolve(id, "read-config", "v1", TaskStatus.RUNNING_STEP, "prod"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
