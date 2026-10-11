package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 模型网关稳定错误，错误消息不包含 prompt、response 或敏感值。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
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
