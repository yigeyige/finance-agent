package cn.com.crc.ems.finance.agent.harness.web;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceAttachment;

/**
 *
 * @Description: 校验业务处理初稿附件并提取只在当前请求内使用的文本投影。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 10:30:00
 */
public final class BusinessGuidanceAttachmentExtractor {

    static final int MAX_ATTACHMENT_COUNT = 3;
    static final long MAX_ATTACHMENT_BYTES = 1_024L * 1_024L;
    static final int MAX_MODEL_CONTENT_CHARS = 24_000;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt", "md", "csv", "json");

    /**
     *
     * @description: 将经格式、大小和编码校验的上传文件转换为受限模型文本投影。
     *
     * @param: files 本次初稿请求上传的文件
     * @return: 不包含原始二进制内容的临时附件投影
     * @throws: BusinessGuidanceAttachmentException 文件不符合受限附件规则时抛出
     * @author: yigeyige99
     */
    public List<BusinessGuidanceAttachment> extract(List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.size() > MAX_ATTACHMENT_COUNT) {
            throw new BusinessGuidanceAttachmentException("最多可上传 3 个附件");
        }
        List<BusinessGuidanceAttachment> attachments = new ArrayList<>();
        int remaining = MAX_MODEL_CONTENT_CHARS;
        for (MultipartFile file : files) {
            String fileName = requireFileName(file);
            if (file.isEmpty() || file.getSize() <= 0) {
                throw new BusinessGuidanceAttachmentException("附件不能为空");
            }
            if (file.getSize() > MAX_ATTACHMENT_BYTES) {
                throw new BusinessGuidanceAttachmentException("单个附件不能超过 1 MB");
            }
            String text = decodeUtf8(file);
            if (text.isBlank()) {
                throw new BusinessGuidanceAttachmentException("附件不能为空");
            }
            boolean truncated = text.length() > remaining;
            String content = text.substring(0, Math.min(text.length(), remaining));
            if (content.isBlank()) {
                break;
            }
            attachments.add(new BusinessGuidanceAttachment(fileName, content, truncated));
            remaining -= content.length();
            if (remaining == 0) {
                break;
            }
        }
        if (attachments.isEmpty()) {
            throw new BusinessGuidanceAttachmentException("附件内容超出模型分析限制");
        }
        return List.copyOf(attachments);
    }

    /**
     *
     * @description: 校验文件名和扩展名，拒绝路径片段与不受支持的二进制文件。
     *
     * @param: file 浏览器上传的附件
     * @return: 可安全展示给模型的文件名
     * @throws: BusinessGuidanceAttachmentException 文件名或格式不合法时抛出
     * @author: yigeyige99
     */
    private static String requireFileName(MultipartFile file) {
        String fileName = file == null ? null : file.getOriginalFilename();
        if (fileName == null || fileName.isBlank() || fileName.length() > 255 || fileName.contains("/")
                || fileName.contains("\\") || fileName.chars().anyMatch(Character::isISOControl)) {
            throw new BusinessGuidanceAttachmentException("附件文件名不合法");
        }
        int extensionStart = fileName.lastIndexOf('.');
        String extension = extensionStart < 1 ? "" : fileName.substring(extensionStart + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessGuidanceAttachmentException("仅支持 txt、md、csv、json 格式附件");
        }
        return fileName;
    }

    /**
     *
     * @description: 使用报告模式解码 UTF-8，避免替换损坏字节后再把内容送入模型。
     *
     * @param: file 已通过大小校验的附件
     * @return: 经过 UTF-8 严格解码的文本
     * @throws: BusinessGuidanceAttachmentException 读取或编码不合法时抛出
     * @author: yigeyige99
     */
    private static String decodeUtf8(MultipartFile file) {
        try {
            return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(file.getBytes())).toString();
        } catch (CharacterCodingException exception) {
            throw new BusinessGuidanceAttachmentException("附件必须使用 UTF-8 文本编码");
        } catch (IOException exception) {
            throw new BusinessGuidanceAttachmentException("附件读取失败，请重新选择后重试");
        }
    }
}
