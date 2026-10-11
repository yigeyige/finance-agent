package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 业务处理初稿中可送入模型的临时附件文本投影，不承载原始文件。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 10:30:00
 */
public record BusinessGuidanceAttachment(String fileName, String content, boolean truncated) {

    public BusinessGuidanceAttachment {
        if (fileName == null || fileName.isBlank() || fileName.length() > 255) {
            throw new IllegalArgumentException("attachment fileName is invalid");
        }
        if (content == null || content.isBlank() || content.length() > 24_000) {
            throw new IllegalArgumentException("attachment content is invalid");
        }
    }
}
