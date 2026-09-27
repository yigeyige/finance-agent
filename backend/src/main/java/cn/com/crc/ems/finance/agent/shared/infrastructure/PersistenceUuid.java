package cn.com.crc.ems.finance.agent.shared.infrastructure;

import java.util.UUID;

import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/**
 *
 * @Description: 统一处理 MyBatis-Plus 字符串主键与领域 UUID 之间的持久化映射。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 22:35:00
 */
public final class PersistenceUuid {

    private PersistenceUuid() {
    }

    /**
     *
     * @description: 将领域 UUID 转换为可由 MyBatis-Plus ASSIGN_UUID 使用的字符串。
     *
     * @param: value 领域 UUID，可以为空。
     * @return: 标准 UUID 字符串，空值返回 null。
     * @author: yigeyige99
     */
    public static String toStorage(UUID value) {
        return UuidGenerator.toStorage(value);
    }

    /**
     *
     * @description: 将数据库返回的 UUID 文本转换为领域 UUID，兼容带连字符和紧凑格式。
     *
     * @param: value 数据库 UUID 文本，可以为空。
     * @return: 领域 UUID，空值返回 null。
     * @author: yigeyige99
     */
    public static UUID toDomain(String value) {
        return UuidGenerator.parse(value);
    }
}
