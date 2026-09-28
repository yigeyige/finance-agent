package cn.com.crc.ems.finance.agent.source.application;

/** 真实来源未配置时的 fail-closed 异常。 */
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
