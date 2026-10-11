package cn.com.crc.ems.finance.agent.shared.application;

/**
 *
 * @Description: 不携带敏感底层文本的统一领域异常。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public class DomainException extends RuntimeException {

    private final DomainErrorCode errorCode;

    public DomainException(DomainErrorCode errorCode) {
        super(errorCode.safeMessage());
        this.errorCode = errorCode;
    }

    public DomainErrorCode errorCode() {
        return errorCode;
    }
}
