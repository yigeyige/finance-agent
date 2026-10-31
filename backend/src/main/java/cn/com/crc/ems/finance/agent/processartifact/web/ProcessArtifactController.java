package cn.com.crc.ems.finance.agent.processartifact.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.processartifact.application.ProcessArtifactService;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactBatch;
import cn.com.crc.ems.finance.agent.processartifact.domain.ProcessArtifactFile;

/** 用户主动生成、查看与下载 EMS 流程文件。 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/process-artifacts")
public final class ProcessArtifactController {
    private static final Set<String> KINDS = Set.of("MODEL", "NODE_PARAMETERS", "DEPARTMENT_APPROVALS",
            "APPROVAL_RULES");
    private final ProcessArtifactService service;

    public ProcessArtifactController(ProcessArtifactService service) {
        this.service = service;
    }

    @PostMapping
    public ProcessArtifactBatchResponse generate(@PathVariable UUID taskId, @RequestBody GenerateRequest request) {
        return ProcessArtifactBatchResponse.from(service.generate(taskId, request.packetId()));
    }

    @GetMapping
    public List<ProcessArtifactBatchResponse> recent(@PathVariable UUID taskId) {
        return service.recent(taskId).stream().map(ProcessArtifactBatchResponse::from).toList();
    }

    @GetMapping("/{batchId}/files/{fileId}")
    public ResponseEntity<byte[]> file(@PathVariable UUID taskId, @PathVariable UUID batchId,
            @PathVariable UUID fileId) {
        ProcessArtifactFile file = batch(taskId, batchId).files().stream()
                .filter(item -> item.fileId().equals(fileId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("文件不存在或已过期"));
        return download(file.contentText().getBytes(StandardCharsets.UTF_8), file.fileName(),
                MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/{batchId}/zip")
    public ResponseEntity<byte[]> zip(@PathVariable UUID taskId, @PathVariable UUID batchId,
            @RequestParam(required = false) List<String> kinds) {
        ProcessArtifactBatch batch = batch(taskId, batchId);
        if (!"IMPORT_CANDIDATE".equals(batch.importStatus())) {
            throw new IllegalStateException("流程结构待修复，暂不能打包导入 ZIP");
        }
        Set<String> selected = kinds == null || kinds.isEmpty() ? Set.of("MODEL") : Set.copyOf(kinds);
        if (!KINDS.containsAll(selected) || !selected.contains("MODEL")) {
            throw new IllegalStateException("打包文件类型不正确，模型文件必须包含");
        }
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
                for (ProcessArtifactFile file : batch.files()) {
                    if (!selected.contains(file.kind())) continue;
                    zip.putNextEntry(new ZipEntry(file.fileName()));
                    zip.write(file.contentText().getBytes(StandardCharsets.UTF_8));
                    zip.closeEntry();
                }
            }
            ProcessArtifactFile model = batch.files().stream().filter(file -> "MODEL".equals(file.kind()))
                    .findFirst().orElseThrow(() -> new IllegalStateException("模型文件不存在"));
            String base = model.fileName().replace("BPMN.xml", "");
            return download(output.toByteArray(), base + ".zip", MediaType.parseMediaType("application/zip"));
        } catch (IOException exception) {
            throw new IllegalStateException("文件打包失败", exception);
        }
    }

    private ProcessArtifactBatch batch(UUID taskId, UUID batchId) {
        return service.recent(taskId).stream().filter(item -> item.batchId().equals(batchId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("文件批次不存在或已过期"));
    }

    private static ResponseEntity<byte[]> download(byte[] content, String name, MediaType type) {
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok().contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(content);
    }

    public record GenerateRequest(UUID packetId) { }
}
