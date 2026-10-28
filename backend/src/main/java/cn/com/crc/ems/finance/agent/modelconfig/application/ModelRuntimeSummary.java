package cn.com.crc.ems.finance.agent.modelconfig.application;

/**
 *
 * @Description: 工作台模型运行摘要，仅包含模式与公开模型名称，不包含连接或凭据。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-29 01:00:00
 */
public record ModelRuntimeSummary(String mode, String modelName) {
}
