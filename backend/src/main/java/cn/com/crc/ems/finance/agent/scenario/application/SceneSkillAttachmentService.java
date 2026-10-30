package cn.com.crc.ems.finance.agent.scenario.application;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cn.com.crc.ems.finance.agent.harness.web.BusinessGuidanceAttachmentExtractor;
import cn.com.crc.ems.finance.agent.scenario.domain.SceneSkillAttachment;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/**
 *
 * @Description: 维护业务场景 Skill 的可复用知识附件，上传时提取完整文本投影并按摘要供模型按需使用。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
@Service
public class SceneSkillAttachmentService {

    private static final int MAX_ATTACHMENTS = 5;

    private final SceneSkillAttachmentRepository attachmentRepository;
    private final BusinessSceneSkillRepository skillRepository;
    private final BusinessGuidanceAttachmentExtractor extractor = new BusinessGuidanceAttachmentExtractor();
    private final Clock clock;

    public SceneSkillAttachmentService(SceneSkillAttachmentRepository attachmentRepository,
            BusinessSceneSkillRepository skillRepository, Clock clock) {
        this.attachmentRepository = attachmentRepository;
        this.skillRepository = skillRepository;
        this.clock = clock;
    }

    /** 读取场景全部附件。 */
    @Transactional(readOnly = true)
    public List<SceneSkillAttachment> list(UUID skillId) {
        return attachmentRepository.findBySkillId(skillId);
    }

    /** 上传附件：校验场景有效、数量与大小，提取完整文本投影并保存。 */
    @Transactional
    public SceneSkillAttachment upload(UUID skillId, MultipartFile file, String summary, UUID operatorId) {
        requireSceneSkill(skillId);
        if (attachmentRepository.countBySkillId(skillId) >= MAX_ATTACHMENTS) {
            throw new IllegalArgumentException("每个业务场景最多上传 5 个附件");
        }
        if (summary == null || summary.isBlank() || summary.length() > 500) {
            throw new IllegalArgumentException("请填写附件摘要");
        }
        String fileName = requireFileName(file);
        String contentText = extractor.extractSceneAttachmentText(file);
        Instant now = Instant.now(clock);
        SceneSkillAttachment attachment = new SceneSkillAttachment(UuidGenerator.newUuid(), skillId, fileName,
                contentTypeOf(fileName), summary.trim(), contentText, operatorId, now, operatorId, now);
        attachmentRepository.insert(attachment);
        return attachment;
    }

    /** 删除场景附件。 */
    @Transactional
    public void delete(UUID skillId, UUID attachmentId) {
        if (!attachmentRepository.deleteById(skillId, attachmentId)) {
            throw new IllegalArgumentException("附件不存在或已删除");
        }
    }

    private void requireSceneSkill(UUID skillId) {
        if (skillRepository.findActiveById(skillId).isEmpty()) {
            throw new IllegalArgumentException("业务场景 Skill 不存在或已失效");
        }
    }

    private static String requireFileName(MultipartFile file) {
        String fileName = file == null ? null : file.getOriginalFilename();
        if (fileName == null || fileName.isBlank() || fileName.length() > 255) {
            throw new IllegalArgumentException("附件文件名不合法");
        }
        return fileName;
    }

    private static String contentTypeOf(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return switch (extension) {
            case "json" -> "application/json";
            case "xml" -> "text/xml";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            case "csv" -> "text/csv";
            case "md" -> "text/markdown";
            default -> "text/plain";
        };
    }
}
