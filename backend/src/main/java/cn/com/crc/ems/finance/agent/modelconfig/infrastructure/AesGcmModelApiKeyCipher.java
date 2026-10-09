package cn.com.crc.ems.finance.agent.modelconfig.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;

/** 使用部署级主密钥完成 AES-GCM API Key 加密，密文不进入日志或 API。 */
@Component
public final class AesGcmModelApiKeyCipher implements ModelApiKeyCipher {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final byte[] key;

    public AesGcmModelApiKeyCipher(@Value("${finance-agent.security.model-secret-key:}") String secret) {
        this.key = secret != null && secret.length() >= 32 ? secret.substring(0, 32).getBytes(StandardCharsets.UTF_8) : null;
    }

    @Override
    public String encrypt(String plaintext) { return crypt(Cipher.ENCRYPT_MODE, plaintext, null); }
    @Override
    public String decrypt(String ciphertext) { return crypt(Cipher.DECRYPT_MODE, ciphertext, null); }
    private String crypt(int mode, String value, byte[] ignored) {
        try {
            if (key == null) throw new IllegalStateException("模型密钥加密主密钥未配置或长度不足");
            byte[] nonce;
            byte[] data;
            if (mode == Cipher.ENCRYPT_MODE) { nonce = new byte[12]; RANDOM.nextBytes(nonce); data = value.getBytes(StandardCharsets.UTF_8); }
            else { byte[] encoded = Base64.getDecoder().decode(value); nonce = java.util.Arrays.copyOf(encoded, 12); data = java.util.Arrays.copyOfRange(encoded, 12, encoded.length); }
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(mode, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, nonce));
            byte[] result = cipher.doFinal(data);
            return mode == Cipher.ENCRYPT_MODE ? Base64.getEncoder().encodeToString(concat(nonce, result)) : new String(result, StandardCharsets.UTF_8);
        } catch (Exception exception) { throw new IllegalStateException("模型密钥加密处理失败", exception); }
    }
    private static byte[] concat(byte[] first, byte[] second) { byte[] merged = new byte[first.length + second.length]; System.arraycopy(first, 0, merged, 0, first.length); System.arraycopy(second, 0, merged, first.length, second.length); return merged; }
}
