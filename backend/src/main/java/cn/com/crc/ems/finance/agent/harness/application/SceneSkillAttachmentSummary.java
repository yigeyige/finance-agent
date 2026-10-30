package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 送入模型第一轮的业务场景附件清单项，仅含文件名与管理员填写的摘要，不含正文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-30 10:00:00
 */
public record SceneSkillAttachmentSummary(String fileName, String summary) {

    public SceneSkillAttachmentSummary {
        if (fileName == null || fileName.isBlank() || fileName.length() > 255) {
            throw new IllegalArgumentException("attachment fileName is invalid");
        }
        if (summary == null || summary.isBlank() || summary.length() > 500) {
            throw new IllegalArgumentException("attachment summary is invalid");
        }
    }
}
