package cn.com.crc.ems.finance.agent.harness.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import cn.com.crc.ems.finance.agent.model.application.BusinessModelInteractionService;

/**
 *
 * @Description: 面向 OPERATOR 的调查方案生成和脱敏证据分析模型入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-06 23:00:00
 */
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/model-interactions")
public final class BusinessModelController {

    private final BusinessModelInteractionService interactionService;
    private final BusinessGuidanceAttachmentExtractor attachmentExtractor;

    public BusinessModelController(BusinessModelInteractionService interactionService) {
        this(interactionService, new BusinessGuidanceAttachmentExtractor());
    }

    BusinessModelController(BusinessModelInteractionService interactionService,
            BusinessGuidanceAttachmentExtractor attachmentExtractor) {
        this.interactionService = interactionService;
        this.attachmentExtractor = attachmentExtractor;
    }

    /**
     *
     * @description: 生成业务处理初稿或按管理员自然语言意见修订当前草稿。
     *
     * @param: taskId 当前任务标识
     * @param: request 管理员消息与受限当前草稿，初稿请求可以为空
     * @return: 可回填表单的模型建议和非敏感调用元数据
     * @author: yigeyige99
     */
    @PostMapping("/guidance")
    public BusinessModelResponse.Guidance guidance(@PathVariable UUID taskId,
            @RequestBody(required = false) BusinessGuidanceRequest request) {
        String message = request == null ? null : request.message();
        return BusinessModelResponse.Guidance.from(interactionService.guideBusinessHandling(taskId, message,
                request == null ? null : request.toCurrentDraft()));
    }

    /**
     *
     * @description: 用经后端校验的临时文本附件生成业务处理初稿，不持久化附件内容。
     *
     * @param: taskId 当前任务标识
     * @param: files 本次模型分析所选的受支持文本附件
     * @return: 可回填表单的模型建议和非敏感调用元数据
     * @author: yigeyige99
     */
    @PostMapping(value = "/guidance/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BusinessModelResponse.Guidance guidanceWithAttachments(@PathVariable UUID taskId,
            @RequestParam("files") List<MultipartFile> files) {
        return BusinessModelResponse.Guidance.from(interactionService.guideBusinessHandling(taskId, null, null,
                attachmentExtractor.extract(files)));
    }

    /** 生成当前范围的模型调查方案，不直接保存或批准步骤。 */
    @PostMapping("/plan")
    public BusinessModelResponse.Plan plan(@PathVariable UUID taskId) {
        return BusinessModelResponse.Plan.from(interactionService.generatePlan(taskId));
    }

    /** 分析当前任务的脱敏证据摘要，不直接生成或发布决策包。 */
    @PostMapping("/evidence-analysis")
    public BusinessModelResponse.Analysis analyzeEvidence(@PathVariable UUID taskId) {
        return BusinessModelResponse.Analysis.from(interactionService.analyzeEvidence(taskId));
    }
}
