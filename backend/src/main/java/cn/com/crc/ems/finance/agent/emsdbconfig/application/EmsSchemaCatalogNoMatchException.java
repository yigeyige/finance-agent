package cn.com.crc.ems.finance.agent.emsdbconfig.application;

/**
 * @Description: 已同步实例目录无关键词候选时，明确终止本次模型分析。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 20:00:00
 */
public final class EmsSchemaCatalogNoMatchException extends RuntimeException {
    public EmsSchemaCatalogNoMatchException() {
        super("当前数据库实例的结构目录未找到相关配置表，请补充业务说明或联系管理员同步表结构");
    }
}
