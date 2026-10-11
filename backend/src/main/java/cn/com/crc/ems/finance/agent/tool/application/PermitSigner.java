package cn.com.crc.ems.finance.agent.tool.application;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import cn.com.crc.ems.finance.agent.task.application.ExecutionPermit;

/**
 *
 * @Description: 进程内 HMAC-SHA256 permit 签名器，密钥不出 JVM。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Component
public class PermitSigner {

    private final byte[] processKey = randomKey();

    public String sign(ExecutionPermit permit, String toolCode, String toolVersion) {
        return encode(mac(canonical(permit, toolCode, toolVersion)));
    }

    public boolean verify(SignedToolPermit signedPermit) {
        if (signedPermit == null) {
            return false;
        }
        String expected = sign(signedPermit.permit(), signedPermit.toolCode(), signedPermit.toolVersion());
        return java.security.MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                signedPermit.signature().getBytes(StandardCharsets.UTF_8));
    }

    private String canonical(ExecutionPermit permit, String toolCode, String toolVersion) {
        return permit.taskId() + "|" + permit.stepId() + "|" + permit.approvalId() + "|"
                + permit.scopeRevisionId() + "|" + permit.sourceBindingId() + "|" + permit.approvalDigest() + "|"
                + permit.idempotencyKey() + "|" + permit.issuedAt() + "|" + permit.expiresAt() + "|" + toolCode
                + "|" + toolVersion;
    }

    private byte[] mac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(processKey, "HmacSHA256"));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("permit signer unavailable", exception);
        }
    }

    private static byte[] randomKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return key;
    }

    private static String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}
