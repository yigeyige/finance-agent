package cn.com.crc.ems.finance.agent.evidence.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import cn.com.crc.ems.finance.agent.evidence.domain.CoverageCompleteness;
import cn.com.crc.ems.finance.agent.evidence.domain.CoverageRecord;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;

/** 将覆盖度遍历结果组装为现有证据域记录。 */
@Service
public class CoverageRecordFactory {

    /**
     * 
     * @description: 组装包含分页、路径、水位和摘要的覆盖度证据记录
     *
     * @param: coverageId 覆盖记录标识
     * @param: taskId 任务标识
     * @param: stepId 步骤标识
     * @param: scopeRevisionId 范围版本标识
     * @param: queryTemplateVersion 查询模板版本
     * @param: queryConditionDigest 查询条件摘要
     * @param: enumerationMethod 枚举方法标识
     * @param: result 分页枚举结果
     * @param: checkedAt 检查时间
     * @param: actorId 审计操作者
     * @return: 可持久化的覆盖度记录
     * @author: yigeyige99
     */
    public CoverageRecord create(UUID coverageId, UUID taskId, UUID stepId, UUID scopeRevisionId,
            String queryTemplateVersion, String queryConditionDigest, String enumerationMethod,
            CoverageEnumerationResult<?> result, Instant checkedAt, UUID actorId) {
        if (result == null || checkedAt == null) {
            throw new IllegalArgumentException("coverage result and checkedAt are required");
        }
        CoverageCompleteness completeness = new CoverageValidator().validate(result);
        return new CoverageRecord(coverageId, taskId, stepId, scopeRevisionId, queryTemplateVersion,
                queryConditionDigest, enumerationMethod, result.pages().size(), result.expectedTotal(),
                result.observedTotal(), jsonArray(result.traversedPaths()), jsonArray(result.uncoveredPaths()),
                jsonArray(result.failures()),
                result.sourceWatermark(), result.resultSetDigest(), completeness, checkedAt, actorId,
                checkedAt, actorId, checkedAt);
    }

    /** 将列表编码为 PostgreSQL jsonb 列可直接持久化的确定性 JSON 数组。 */
    private static String jsonArray(List<String> values) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        values.forEach(array::add);
        return CanonicalJsonCodec.canonicalize(array);
    }
}
