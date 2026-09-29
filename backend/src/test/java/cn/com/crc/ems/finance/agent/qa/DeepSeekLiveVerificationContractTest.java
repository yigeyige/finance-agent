package cn.com.crc.ems.finance.agent.qa;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/** 校验 DeepSeek 真实模型手动验证入口保持离线默认和 metadata-only 记录。 */
class DeepSeekLiveVerificationContractTest {

    @Test
    void liveScriptIsOfflineByDefaultAndDoesNotPersistSensitiveContent() throws IOException {
        String script = readRepoFile("scripts/live-deepseek-test.ps1");
        String guide = readRepoFile("docs/deepseek-live-test.md");
        String template = readRepoFile("scripts/fixtures/deepseek-live-result-template.json");
        assertThat(script).contains("$Execute", "networkCalls=0", "apiKeyRead=0", "DEEPSEEK_API_KEY",
                "responseId", "totalTokens", "schemaStatus");
        assertThat(script).doesNotContain("responseBody", "reasoningContent", "Set-Content -LiteralPath $Response");
        assertThat(guide).contains("Remove-Item Env:DEEPSEEK_API_KEY", "不保存 prompt", "reasoning");
        assertThat(template).contains("PENDING_USER_RUN", "schemaStatus", "limitations");
    }

    @Test
    void stubAdapterContractRemainsAvailableWithoutLiveKey() throws IOException {
        String adapterTest = readRepoFile("backend/src/test/java/cn/com/crc/ems/finance/agent/harness/DeepSeekOpenAiAdapterTest.java");
        assertThat(adapterTest).contains("HttpServer", "deepseek-flash", "stream", "response-reasoning-marker")
                .doesNotContain("DEEPSEEK_API_KEY");
    }

    private static String readRepoFile(String relativePath) throws IOException {
        return Files.readString(Path.of("..", relativePath), StandardCharsets.UTF_8);
    }
}
