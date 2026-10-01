package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 定义只读来源适配器失败关闭时可供调用方稳定识别的错误码。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
public enum SourceAdapterErrorCode {
    SOURCE_NOT_CONFIGURED,
    SOURCE_CATALOG_REQUIRED,
    SOURCE_QUERY_REJECTED,
    SOURCE_CATALOG_DRIFT,
    SOURCE_UNAVAILABLE
}
