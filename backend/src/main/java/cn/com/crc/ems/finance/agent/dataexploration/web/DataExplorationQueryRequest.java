package cn.com.crc.ems.finance.agent.dataexploration.web;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 *
 * @Description: 数据探索提交的单条 SQL 请求，只允许服务端进一步校验后执行。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-16 15:30:00
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record DataExplorationQueryRequest(@NotBlank @Size(max = 4000) String sql, UUID configId) { }
