package cn.com.crc.ems.finance.agent.shared.application;

/** 不携带敏感底层文本的统一领域异常。 */
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
