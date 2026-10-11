package cn.com.crc.ems.finance.agent.shared.domain;

import java.util.UUID;

/**
 *
 * @Description: 统一生成和解析应用领域 UUID。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public final class UuidGenerator {

    private UuidGenerator() {
    }

    /** 生成新的随机 UUID。 */
    public static UUID newUuid() {
        return UUID.randomUUID();
    }

    /** 解析标准或 MyBatis-Plus 无连字符 UUID 文本。 */
    public static UUID parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() == 32) {
            normalized = normalized.substring(0, 8) + "-" + normalized.substring(8, 12) + "-"
                    + normalized.substring(12, 16) + "-" + normalized.substring(16, 20) + "-"
                    + normalized.substring(20);
        }
        return UUID.fromString(normalized);
    }

    /** 将领域 UUID 转为持久化文本。 */
    public static String toStorage(UUID value) {
        return value == null ? null : value.toString();
    }
}
