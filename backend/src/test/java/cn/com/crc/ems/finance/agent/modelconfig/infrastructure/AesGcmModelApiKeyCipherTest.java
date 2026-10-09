package cn.com.crc.ems.finance.agent.modelconfig.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** 验证模型 API Key 新旧密文格式及认证失败边界。 */
class AesGcmModelApiKeyCipherTest {
    @Test void encryptsWithVersionedDerivedKey() { var cipher = new AesGcmModelApiKeyCipher("模型部署主密钥用于内网安全保存配置请稳定保管不要泄露20261009"); String encrypted = cipher.encrypt("api-key"); assertThat(encrypted).startsWith("v2:"); assertThat(cipher.decrypt(encrypted)).isEqualTo("api-key"); }
    @Test void rejectsTamperedCiphertext() { var cipher = new AesGcmModelApiKeyCipher("01234567890123456789012345678901"); String encrypted = cipher.encrypt("api-key"); assertThatThrownBy(() -> cipher.decrypt(encrypted.substring(0, encrypted.length() - 2) + "aa")).isInstanceOf(IllegalStateException.class).hasMessage("模型密钥加密处理失败"); }
}
