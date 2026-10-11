package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessGuidanceContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;

/**
 *
 * @Description: 将模型的可选查询建议置于连接配置与只读 SQL 守卫之后执行。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class EmsDatabaseAssistanceService {
    private final EmsDatabaseConnectionConfigService configService;
    private final EmsReadOnlySqlGuard sqlGuard;
    private final EmsDatabaseQueryExecutor executor;

    public EmsDatabaseAssistanceService(EmsDatabaseConnectionConfigService configService, EmsReadOnlySqlGuard sqlGuard,
            EmsDatabaseQueryExecutor executor) {
        this.configService = configService;
        this.sqlGuard = sqlGuard;
        this.executor = executor;
    }

    /** 无启用连接、不需查询或任一受控失败时静默降级，不影响原有模型指导。 */
    public List<EmsDatabaseEvidence> collect(BusinessModelClient modelClient, BusinessGuidanceContext context) {
        try {
            Optional<cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseQueryRequest> request = modelClient
                    .planEmsDatabaseQuery(context);
            if (request.isEmpty()) return List.of();
            EmsDatabaseConnectionConfig config = configService.requireEnabled();
            String sql = sqlGuard.validate(request.get().sql());
            EmsDatabaseQueryResult result = executor.execute(config, sql);
            return List.of(new EmsDatabaseEvidence(request.get().purpose(), result.columns(), result.rows()));
        } catch (RuntimeException exception) {
            return List.of();
        }
    }
}
