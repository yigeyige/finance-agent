package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 真实来源未配置时的 fail-closed 异常。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public class SourceAdapterUnavailableException extends IllegalStateException {

    private final SourceAdapterErrorCode code;

    public SourceAdapterUnavailableException(SourceAdapterErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public SourceAdapterErrorCode code() {
        return code;
    }
}
