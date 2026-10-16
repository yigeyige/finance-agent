package cn.com.crc.ems.finance.agent.emsdbconfig.application;

/**
 *
 * @Description: EMS 表中可安全展示的字段结构摘要。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
public record EmsDatabaseColumnMetadata(String name, String type, String comment) {
    public EmsDatabaseColumnMetadata {
        if (name == null || name.isBlank() || type == null || type.isBlank()) {
            throw new IllegalArgumentException("EMS 字段元数据不合法");
        }
        comment = comment == null ? "" : comment;
    }
}
