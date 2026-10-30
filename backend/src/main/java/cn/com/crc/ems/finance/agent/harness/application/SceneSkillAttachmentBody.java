package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 送入模型第二轮的场景附件完整正文投影，按需注入且不按模型输入长度截断。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
public record SceneSkillAttachmentBody(String fileName, String content) {

    public SceneSkillAttachmentBody {
        if (fileName == null || fileName.isBlank() || fileName.length() > 255) {
            throw new IllegalArgumentException("attachment fileName is invalid");
        }
        if (content == null || content.isBlank() || content.length() > 200_000) {
            throw new IllegalArgumentException("attachment content is invalid");
        }
    }
}
