package cn.com.crc.ems.finance.agent.modelconfig.application;

/** 对模型 API Key 进行静态加密存储的边界。 */
public interface ModelApiKeyCipher {
    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}
