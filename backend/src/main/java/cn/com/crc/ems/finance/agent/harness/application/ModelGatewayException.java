package cn.com.crc.ems.finance.agent.harness.application;

/** 模型网关稳定错误，错误消息不包含 prompt、response 或敏感值。 */
public class ModelGatewayException extends RuntimeException {

    private final String code;

    public ModelGatewayException(String code, String message) {
        super(message);
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        this.code = code;
    }

    public String code() {
        return code;
    }
}
