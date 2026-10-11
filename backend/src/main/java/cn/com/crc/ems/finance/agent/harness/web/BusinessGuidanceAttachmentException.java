package cn.com.crc.ems.finance.agent.harness.web;

/**
 *
 * @Description: 表示业务处理附件校验失败的安全业务异常。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 10:30:00
 */
public final class BusinessGuidanceAttachmentException extends RuntimeException {

    public BusinessGuidanceAttachmentException(String message) {
        super(message);
    }
}
