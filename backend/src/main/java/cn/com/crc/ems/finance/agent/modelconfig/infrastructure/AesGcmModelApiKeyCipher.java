package cn.com.crc.ems.finance.agent.modelconfig.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;
import cn.com.crc.ems.finance.agent.shared.application.DomainErrorCode;
import cn.com.crc.ems.finance.agent.shared.application.DomainException;

/**
 *
 * @Description: 使用部署级主密钥完成 AES-GCM API Key 加密，密文不进入日志或 API。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public final class AesGcmModelApiKeyCipher implements ModelApiKeyCipher {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String V2_PREFIX = "v2:";
    private final byte[] key;
    private final byte[] legacyKey;

    public AesGcmModelApiKeyCipher(@Value("${finance-agent.security.model-secret-key:}") String secret) {
        this.key = derive(secret);
        this.legacyKey = legacy(secret);
    }

    /**
     *
     * @description: 使用已配置的部署级主密钥加密凭据；缺失主密钥时安全拒绝保存。
     *
     * @param: plaintext 待加密凭据
     * @return: 带版本前缀的 AES-GCM 密文
     * @throws: DomainException 部署未配置有效主密钥时抛出
     * @author: yigeyige99
     */
    @Override
    public String encrypt(String plaintext) {
        requireConfiguredKey(key);
        return V2_PREFIX + crypt(Cipher.ENCRYPT_MODE, plaintext, key);
    }

    /**
     *
     * @description: 使用与密文版本匹配的部署级主密钥解密凭据。
     *
     * @param: ciphertext 已保存的凭据密文
     * @return: 仅在运行时使用的明文凭据
     * @throws: DomainException 部署未配置有效主密钥时抛出
     * @author: yigeyige99
     */
    @Override
    public String decrypt(String ciphertext) {
        byte[] selectedKey = ciphertext.startsWith(V2_PREFIX) ? key : legacyKey;
        requireConfiguredKey(selectedKey);
        return crypt(Cipher.DECRYPT_MODE, ciphertext.startsWith(V2_PREFIX) ? ciphertext.substring(V2_PREFIX.length()) : ciphertext,
                selectedKey);
    }
    private String crypt(int mode, String value, byte[] cipherKey) {
        try {
            byte[] nonce;
            byte[] data;
            if (mode == Cipher.ENCRYPT_MODE) { nonce = new byte[12]; RANDOM.nextBytes(nonce); data = value.getBytes(StandardCharsets.UTF_8); }
            else { byte[] encoded = Base64.getDecoder().decode(value); nonce = java.util.Arrays.copyOf(encoded, 12); data = java.util.Arrays.copyOfRange(encoded, 12, encoded.length); }
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(mode, new SecretKeySpec(cipherKey, "AES"), new GCMParameterSpec(128, nonce));
            byte[] result = cipher.doFinal(data);
            return mode == Cipher.ENCRYPT_MODE ? Base64.getEncoder().encodeToString(concat(nonce, result)) : new String(result, StandardCharsets.UTF_8);
        } catch (Exception exception) { throw new IllegalStateException("模型密钥加密处理失败", exception); }
    }
    private static void requireConfiguredKey(byte[] cipherKey) {
        if (cipherKey == null) throw new DomainException(DomainErrorCode.CONFIGURATION_UNAVAILABLE);
    }
    private static byte[] derive(String secret) { try { return secret == null || secret.length() < 32 ? null : MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8)); } catch (Exception exception) { throw new IllegalStateException("模型密钥加密处理失败", exception); } }
    private static byte[] legacy(String secret) { return secret != null && secret.length() >= 32 ? secret.substring(0, 32).getBytes(StandardCharsets.UTF_8) : null; }
    private static byte[] concat(byte[] first, byte[] second) { byte[] merged = new byte[first.length + second.length]; System.arraycopy(first, 0, merged, 0, first.length); System.arraycopy(second, 0, merged, first.length, second.length); return merged; }
}
