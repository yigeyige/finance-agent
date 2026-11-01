package cn.com.crc.ems.finance.agent.task.application;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceAttachment;
import cn.com.crc.ems.finance.agent.task.domain.TaskAttachment;

/**
 *
 * @Description: 首次模型分析前把上传附件写入任务附件库，保存原始文件、文本投影和摘要信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 23:40:00
 */
@Service
public class TaskAttachmentService {

    private final TaskAttachmentRepository repository;
    private final Clock clock;

    public TaskAttachmentService(TaskAttachmentRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    /**
     *
     * @description: 保存本次上传附件；同一任务内相同 SHA-256 去重，落库失败直接抛出。
     *
     * @param: taskId 任务标识
     * @param: files 原始上传文件
     * @param: projections 模型实际使用的文本投影
     * @return: 本次新增的附件记录
     * @throws: IllegalStateException 读取文件或落库失败时抛出
     * @author: yigeyige99
     */
    public List<TaskAttachment> persist(UUID taskId, List<MultipartFile> files,
            List<BusinessGuidanceAttachment> projections) {
        Map<String, BusinessGuidanceAttachment> byName = projections.stream()
                .collect(Collectors.toMap(BusinessGuidanceAttachment::fileName, Function.identity(),
                        (first, second) -> first));
        List<TaskAttachment> saved = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = safeName(file.getOriginalFilename());
            byte[] content = readBytes(file);
            String sha256 = sha256(content);
            if (repository.existsByTaskIdAndSha256(taskId, sha256)) {
                continue;
            }
            BusinessGuidanceAttachment projection = byName.get(fileName);
            TaskAttachment attachment = new TaskAttachment(UUID.randomUUID(), taskId, fileName,
                    contentType(file), content.length, sha256, content,
                    projection == null ? "" : projection.content(),
                    projection == null || projection.truncated(), Instant.now(clock));
            repository.insert(attachment);
            saved.add(attachment);
        }
        return List.copyOf(saved);
    }

    /** 按任务列出声明的附件元数据。 */
    public List<TaskAttachment> list(UUID taskId) {
        return repository.findByTaskId(taskId);
    }

    /** 读取属于指定任务的附件，跨任务访问直接拒绝。 */
    public TaskAttachment require(UUID taskId, UUID attachmentId) {
        return repository.findById(taskId, attachmentId)
                .orElseThrow(() -> new IllegalStateException("任务附件不存在"));
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("读取上传附件失败", exception);
        }
    }

    private static String safeName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "attachment";
        }
        String name = Path.of(originalFilename).getFileName().toString();
        return name.isBlank() ? "attachment" : name;
    }

    private static String contentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType == null || contentType.isBlank() ? "application/octet-stream" : contentType;
    }

    private static String sha256(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("附件摘要计算失败", exception);
        }
    }
}
