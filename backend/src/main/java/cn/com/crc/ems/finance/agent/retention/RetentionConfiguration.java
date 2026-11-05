package cn.com.crc.ems.finance.agent.retention;

import java.nio.file.Path;
import java.time.Clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import cn.com.crc.ems.finance.agent.config.FinanceAgentProperties;

/**
 * @Description: 组装每周清理所需的固定表、目录和日志依赖；不在应用启动时补跑删除。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-04 20:00:00
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
class RetentionConfiguration {
    @Bean
    RetentionDatabaseCleaner retentionDatabaseCleaner(RetentionJdbcRepository repository) {
        return new RetentionDatabaseCleaner(repository, 500);
    }

    @Bean
    RetentionRunLog retentionRunLog(@Value("${FINANCE_AGENT_LOG_ROOT:.runtime/app-logs}") String root) {
        return new RetentionRunLog(Path.of(root));
    }

    @Bean
    RetentionScheduleStore retentionScheduleStore(
            @Value("${FINANCE_AGENT_SCHEDULE_ROOT:.runtime/scheduled-jobs}") String root) {
        return new RetentionScheduleStore(Path.of(root));
    }

    @Bean
    ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("retention-schedule-");
        return scheduler;
    }

    @Bean
    RetentionScheduleService retentionScheduleService(RetentionScheduleStore store,
            ThreadPoolTaskScheduler scheduler, RetentionJob job) {
        return new RetentionScheduleService(store, scheduler, job::execute);
    }

    /** 根据部署环境只启用 local/test 临时对象目录清理。 */
    @Bean
    RetentionJob retentionJob(RetentionDatabaseCleaner database, RetentionRunLog log,
            FinanceAgentProperties properties, Environment environment, Clock clock,
            @Value("${FINANCE_AGENT_LOCAL_LOG_ROOT:}") String localLogRoot) {
        String workspace = properties.getWorkspace().getRoot();
        String objects = properties.getStorage().getTemporaryRoot();
        Path workspaceRoot = workspace == null || workspace.isBlank() ? null : Path.of(workspace);
        Path objectRoot = objects == null || objects.isBlank() ? null : Path.of(objects);
        boolean localObjects = environment.acceptsProfiles(Profiles.of("local", "test"));
        Path localLogs = localLogRoot.isBlank() ? null : Path.of(localLogRoot);
        return new RetentionJob(database, clock, workspaceRoot, objectRoot, localLogs, localObjects, log);
    }
}
