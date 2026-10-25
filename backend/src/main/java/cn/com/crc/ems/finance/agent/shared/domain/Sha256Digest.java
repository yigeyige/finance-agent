package cn.com.crc.ems.finance.agent.shared.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @Description: 基于 JDK 的 UTF-8 SHA-256 摘要工具，作为全项目唯一的摘要入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class Sha256Digest {

    private Sha256Digest() {
    }

    /** 返回输入文本的 SHA-256 摘要的小写十六进制表示。 */
    public static String hex(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is required");
        }
        return java.util.HexFormat.of().formatHex(newDigest().digest(value.getBytes(StandardCharsets.UTF_8)));
    }

    /** 返回输入字节的小写十六进制表示（不进行摘要，供流式摘要的最终编码使用）。 */
    public static String hex(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes is required");
        }
        return java.util.HexFormat.of().formatHex(bytes);
    }

    /** 返回一个新的 SHA-256 摘要实例，供流式内容分块更新。 */
    public static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
