package cn.com.crc.ems.finance.agent.modelconfig.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/**
 *
 * @Description: 工作台模型运行摘要用例，读取运行模式与启用配置并投影为安全公开信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-29 01:00:00
 */
@Service
public class ModelRuntimeSummaryService {
    private final FinanceAgentProperties properties;
    private final ModelConnectionConfigRepository repository;

    public ModelRuntimeSummaryService(FinanceAgentProperties properties, ModelConnectionConfigRepository repository) {
        this.properties = properties;
        this.repository = repository;
    }

    /**
     *
     * @description: 获取运行模式及启用模型名称，未启用真实模型时返回空名称。
     *
     * @return: 不含凭据或连接信息的模型摘要
     * @author: yigeyige99
     */
    @Transactional(readOnly = true)
    public ModelRuntimeSummary current() {
        if (properties.getDeepSeek().isStub()) {
            return new ModelRuntimeSummary("STUB", "本地模拟模型");
        }
        return new ModelRuntimeSummary("LIVE", repository.findEnabled().map(config -> config.modelName()).orElse(null));
    }
}
