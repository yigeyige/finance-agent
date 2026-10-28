package cn.com.crc.ems.finance.agent.emsdbconfig.web;

import java.util.UUID;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;

/**
 *
 * @Description: 供业务处理选择的启用数据库实例只读视图，不暴露连接配置或凭据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 15:00:00
 */
public record EmsDatabaseInstanceResponse(UUID configId, String name) {

    public static EmsDatabaseInstanceResponse from(EmsDatabaseConnectionConfig config) {
        return new EmsDatabaseInstanceResponse(config.configId(), config.name());
    }
}
