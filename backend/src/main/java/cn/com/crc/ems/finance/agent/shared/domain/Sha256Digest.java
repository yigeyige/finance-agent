package cn.com.crc.ems.finance.agent.shared.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 基于 JDK 的 UTF-8 SHA-256 摘要工具。 */
public final class Sha256Digest {

    private Sha256Digest() {
    }

    /** 返回输入文本的小写十六进制 SHA-256 摘要。 */
    public static String hex(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
