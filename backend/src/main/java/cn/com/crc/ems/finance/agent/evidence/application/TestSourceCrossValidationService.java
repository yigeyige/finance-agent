package cn.com.crc.ems.finance.agent.evidence.application;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterErrorCode;
import cn.com.crc.ems.finance.agent.source.application.SourceAdapterUnavailableException;
import cn.com.crc.ems.finance.agent.source.application.SourceCatalog;
import cn.com.crc.ems.finance.agent.source.infrastructure.TestBusinessSourceAdapter;

/**
 *
 * @Description: 仅在 local/test 环境使用两个固定虚构来源执行交叉印证并保留冲突语义。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-01 00:00:00
 */
@Service
@Profile({"local", "test"})
public class TestSourceCrossValidationService {

    public static final String OBJECT_TYPE = "test-personnel-assignment";
    public static final String PRIMARY_SOURCE_ID = "test-business-primary";
    public static final String VERIFICATION_SOURCE_ID = "test-business-verification";

    private final TestBusinessSourceAdapter primarySource;
    private final SourceConflictCoordinator conflictCoordinator;

    /**
     *
     * @description: 注入主测试源目录与既有来源冲突协调器。
     *
     * @param: primarySource 首个测试业务只读源。
     * @param: conflictCoordinator 按目录权威顺序协调候选的服务。
     * @author: yigeyige99
     */
    public TestSourceCrossValidationService(TestBusinessSourceAdapter primarySource,
            SourceConflictCoordinator conflictCoordinator) {
        if (primarySource == null || conflictCoordinator == null) {
            throw new IllegalArgumentException("test source dependencies are required");
        }
        this.primarySource = primarySource;
        this.conflictCoordinator = conflictCoordinator;
    }

    /**
     *
     * @description: 对同一测试对象生成两个独立固定来源候选并执行交叉印证。
     *
     * @param: objectKey 虚构测试对象标识。
     * @param: scenario 固定测试场景。
     * @return: 包含目录版本和冲突协调结果的交叉印证结果。
     * @throws: SourceAdapterUnavailableException 第二来源不可用或目录未冻结时抛出。
     * @author: yigeyige99
     */
    public TestSourceCrossValidationResult validate(String objectKey, TestSourceCrossValidationScenario scenario) {
        if (objectKey == null || objectKey.isBlank() || scenario == null) {
            throw rejected("test source cross-validation input is invalid");
        }
        SourceCatalog catalog = primarySource.catalog();
        requireAuthority(catalog);
        SourceCandidate primary = candidate(PRIMARY_SOURCE_ID, objectKey, "primary");
        if (scenario == TestSourceCrossValidationScenario.VERIFICATION_UNAVAILABLE) {
            throw new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_UNAVAILABLE,
                    "test verification source is unavailable");
        }
        SourceCandidate verification = candidate(VERIFICATION_SOURCE_ID, objectKey,
                scenario == TestSourceCrossValidationScenario.CONFLICT ? "conflict" : "primary");
        List<SourceCandidate> candidates = List.of(primary, verification);
        SourceConflictResult result = scenario == TestSourceCrossValidationScenario.UNKNOWN
                || !primary.resultDigest().equals(verification.resultDigest())
                        ? new SourceConflictResult(null, SourceConflictCode.UNKNOWN_SOURCE_CONFLICT, candidates)
                        : conflictCoordinator.resolve(catalog, candidates);
        return new TestSourceCrossValidationResult(catalog.version(), result);
    }

    /**
     *
     * @description: 校验目录中冻结了两个不同来源及其唯一优先顺序。
     *
     * @param: catalog 主测试源对外声明的来源目录。
     * @throws: SourceAdapterUnavailableException 目录缺失、漂移或未登记第二来源时抛出。
     * @author: yigeyige99
     */
    private static void requireAuthority(SourceCatalog catalog) {
        if (catalog == null || !List.of(PRIMARY_SOURCE_ID, VERIFICATION_SOURCE_ID).equals(catalog.authorityFor(OBJECT_TYPE))) {
            throw new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_CATALOG_DRIFT,
                    "test source cross-validation authority is not registered");
        }
    }

    /**
     *
     * @description: 为固定虚构来源生成不含原始字段的候选摘要。
     *
     * @param: sourceId 冻结来源标识。
     * @param: objectKey 虚构测试对象标识。
     * @param: variant 固定场景区分符。
     * @return: 可参与来源协调的候选摘要。
     * @author: yigeyige99
     */
    private static SourceCandidate candidate(String sourceId, String objectKey, String variant) {
        return new SourceCandidate(sourceId, OBJECT_TYPE, objectKey, Sha256Digest.hex(objectKey + "|" + variant));
    }

    /**
     *
     * @description: 构建不触发单源回退确认的稳定查询拒绝异常。
     *
     * @param: message 安全的失败说明。
     * @return: 带查询拒绝错误码的异常。
     * @author: yigeyige99
     */
    private static SourceAdapterUnavailableException rejected(String message) {
        return new SourceAdapterUnavailableException(SourceAdapterErrorCode.SOURCE_QUERY_REJECTED, message);
    }
}
