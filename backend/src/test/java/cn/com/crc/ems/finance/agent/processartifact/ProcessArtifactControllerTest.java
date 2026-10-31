package cn.com.crc.ems.finance.agent.processartifact;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactService;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactFile;
import cn.com.crc.ems.finance.agent.processartifact.web.ProcessArtifactController;

/** ZIP 始终包含中文名称的模型文件，只包含用户选择的适用配置。 */
class ProcessArtifactControllerTest {
    @Test
    void zipContainsModelAndSelectedConfigOnly() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID batchId = UUID.randomUUID();
        ProcessArtifactBatch batch = new ProcessArtifactBatch(batchId, taskId, UUID.randomUUID(), 1,
                "{}", List.of(), "IMPORT_CANDIDATE", Instant.now(), List.of(
                        new ProcessArtifactFile(UUID.randomUUID(), batchId, "MODEL", "测试流程BPMN.xml", "{}"),
                        new ProcessArtifactFile(UUID.randomUUID(), batchId, "NODE_PARAMETERS", "节点参数配置.json", "[]")));
        var service = mock(ProcessArtifactService.class);
        when(service.recent(taskId)).thenReturn(List.of(batch));
        var controller = new ProcessArtifactController(service);

        byte[] content = controller.zip(taskId, batchId, List.of("MODEL")).getBody();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content))) {
            assertThat(zip.getNextEntry().getName()).isEqualTo("测试流程BPMN.xml");
            assertThat(zip.getNextEntry()).isNull();
        }
        assertThatThrownBy(() -> controller.zip(taskId, batchId, List.of("NODE_PARAMETERS")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("模型文件必须包含");
    }
}
