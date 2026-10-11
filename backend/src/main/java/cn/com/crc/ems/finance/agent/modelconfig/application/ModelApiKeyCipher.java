package cn.com.crc.ems.finance.agent.modelconfig.application;

/**
 *
 * @Description: 对模型 API Key 进行静态加密存储的边界。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface ModelApiKeyCipher {
    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}
