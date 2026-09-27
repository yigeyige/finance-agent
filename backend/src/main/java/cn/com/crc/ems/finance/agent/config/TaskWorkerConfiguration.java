package cn.com.crc.ems.finance.agent.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.com.crc.ems.finance.agent.task.infrastructure.TaskLeaseService;
import cn.com.crc.ems.finance.agent.task.infrastructure.worker.PostgresWorker;
import cn.com.crc.ems.finance.agent.task.infrastructure.worker.WorkerStepHandler;

/**
 *
 * @Description: 按配置懒加载 PostgreSQL Worker，保持 API 默认不创建 Worker 线程。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-28 10:20:00
 */
@Configuration(proxyBeanMethods = false)
public class TaskWorkerConfiguration {

    /** 仅在 Worker 开关打开且后续任务提供步骤处理器时创建生命周期 Bean。 */
    @Bean
    @ConditionalOnProperty(prefix = "finance-agent.worker", name = "enabled", havingValue = "true")
    @ConditionalOnBean(WorkerStepHandler.class)
    public PostgresWorker postgresWorker(TaskLeaseService leaseService, WorkerStepHandler stepHandler,
            FinanceAgentProperties properties) {
        return new PostgresWorker(leaseService, stepHandler, properties.getWorker());
    }
}
