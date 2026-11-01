package cn.com.crc.ems.finance.agent.task.web;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.crc.ems.finance.agent.task.application.TaskAttachmentService;
import cn.com.crc.ems.finance.agent.task.domain.TaskAttachment;

/**
 *
 * @Description: 任务附件留痕查询与原始文件下载接口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/attachments")
public class TaskAttachmentController {

    private final TaskAttachmentService service;

    public TaskAttachmentController(TaskAttachmentService service) {
        this.service = service;
    }

    /** 查询任务附件元数据，不返回正文。 */
    @GetMapping
    public List<TaskAttachmentResponse> list(@PathVariable UUID taskId) {
        return service.list(taskId).stream().map(TaskAttachmentResponse::from).toList();
    }

    /** 下载属于指定任务的原始附件。 */
    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID taskId, @PathVariable UUID attachmentId) {
        TaskAttachment attachment = service.require(taskId, attachmentId);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(attachment.contentType());
        } catch (IllegalArgumentException exception) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok().contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(attachment.fileName(), StandardCharsets.UTF_8).build().toString())
                .body(attachment.content());
    }
}
