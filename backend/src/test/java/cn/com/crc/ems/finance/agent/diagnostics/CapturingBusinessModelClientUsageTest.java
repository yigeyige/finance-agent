package cn.com.crc.ems.finance.agent.diagnostics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelCallMetadata;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelResult;
import cn.com.crc.ems.finance.agent.harness.application.ModelPlanSuggestion;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.usage.application.TokenUsageRecorder;

/**
 * @Description: 验证装饰器只把 LIVE 且 token 大于 0 的调用交给用量端口，并归属当前身份。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 16:20:00
 */
class CapturingBusinessModelClientUsageTest {

    private static final String LIVE = "LIVE";
    private static final String STUB = "STUB";
    private static final String DIGEST = "0".repeat(64);

    @AfterEach
    void clearIdentity() {
        IdentityContext.clear();
    }

    @Test
    void recordsLiveCallForCurrentIdentity() {
        UUID userId = UUID.randomUUID();
        IdentityContext.set(new CurrentIdentity(userId, "subject", "测试用户", Set.of(RoleCode.PLATFORM_USER)));
        BusinessModelClient delegate = mock(BusinessModelClient.class);
        TokenUsageRecorder recorder = mock(TokenUsageRecorder.class);
        BusinessModelResult<ModelPlanSuggestion> liveCall = result(metadata(LIVE, 120));
        when(delegate.generatePlan(null)).thenReturn(liveCall);
        CapturingBusinessModelClient client = client(delegate, recorder);

        client.generatePlan(null);

        verify(recorder).record(eq(userId), isNull(), eq("deepseek-chat"), eq(120), any());
    }

    @Test
    void skipsStubModeAndZeroTokens() {
        IdentityContext.set(new CurrentIdentity(UUID.randomUUID(), "subject", "测试用户",
                Set.of(RoleCode.PLATFORM_USER)));
        BusinessModelClient delegate = mock(BusinessModelClient.class);
        TokenUsageRecorder recorder = mock(TokenUsageRecorder.class);
        BusinessModelResult<ModelPlanSuggestion> stubCall = result(metadata(STUB, 120));
        BusinessModelResult<ModelPlanSuggestion> zeroTokenCall = result(metadata(LIVE, 0));
        when(delegate.generatePlan(null)).thenReturn(stubCall, zeroTokenCall);
        CapturingBusinessModelClient client = client(delegate, recorder);

        client.generatePlan(null);
        client.generatePlan(null);

        verify(recorder, never()).record(any(), any(), any(), anyInt(), any());
    }

    private static CapturingBusinessModelClient client(BusinessModelClient delegate, TokenUsageRecorder recorder) {
        return new CapturingBusinessModelClient(delegate, new LatestExceptionStore(Clock.systemUTC()), null, recorder);
    }

    private static BusinessModelCallMetadata metadata(String mode, int tokens) {
        return new BusinessModelCallMetadata(mode, "deepseek-chat", DIGEST, DIGEST, tokens, 8);
    }

    @SuppressWarnings("unchecked")
    private static BusinessModelResult<ModelPlanSuggestion> result(BusinessModelCallMetadata metadata) {
        return new BusinessModelResult<>(mock(ModelPlanSuggestion.class), metadata);
    }
}
