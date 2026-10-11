package cn.com.crc.ems.finance.agent.emsdbconfig.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: EMS MySQL 连接新增或编辑请求，密码只允许写入。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record EmsDatabaseConnectionConfigRequest(@NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 500) String jdbcUrl, @NotBlank @Size(max = 200) String username,
        @Size(max = 1000) String password, @Min(1) @Max(30) int connectTimeoutSeconds,
        @Min(1) @Max(60) int queryTimeoutSeconds, @Min(1) @Max(100) int maxRows) {
}
