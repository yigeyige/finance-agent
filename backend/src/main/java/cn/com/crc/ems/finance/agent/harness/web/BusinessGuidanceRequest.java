package cn.com.crc.ems.finance.agent.harness.web;

import java.util.List;

import cn.com.crc.ems.finance.agent.harness.application.BusinessHandlingDraft;
import cn.com.crc.ems.finance.agent.harness.application.EmsDatabaseEvidence;

/**
 *
 * @Description: 管理员发起业务处理初稿或会话修订时允许提交的受限请求体。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 21:10:00
 */
public record BusinessGuidanceRequest(String message, Draft currentDraft) {

    /**
     *
     * @description: 将接口层草稿转换为应用层受限值对象，不接受前端额外字段。
     *
     * @return: 当前结构化草稿，初次生成时返回 null
     * @author: yigeyige99
     */
    public BusinessHandlingDraft toCurrentDraft() {
        if (currentDraft == null) {
            return null;
        }
        return new BusinessHandlingDraft(currentDraft.handlingType(), currentDraft.premiseType(),
                currentDraft.statement(),
                currentDraft.objectKey(), currentDraft.startDate(), currentDraft.endDate(),
                currentDraft.investigationAreas(), currentDraft.dataUse(),
                currentDraft.conclusion(), currentDraft.evidence());
    }

    /** 业务处理草稿的接口层字段，保持与前端表单一一对应。 */
    public record Draft(String handlingType, String premiseType, String statement,
            String objectKey, String startDate, String endDate,
            List<String> investigationAreas, String dataUse,
            String conclusion, List<EmsDatabaseEvidence> evidence) {
    }
}
