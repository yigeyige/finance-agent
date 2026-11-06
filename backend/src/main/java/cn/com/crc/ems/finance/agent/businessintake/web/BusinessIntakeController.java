package cn.com.crc.ems.finance.agent.businessintake.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.businessintake.application.BusinessIntakeDraftService;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelTimeoutException;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;

/**
 * @Description: AI 预填只读入口：解析自由文本为业务处理表单字段，不创建任务、不改动既有模型分析接口。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:30:00
 */
@RestController
@RequestMapping("/api/v1/business-intake")
public class BusinessIntakeController {

    private final BusinessIntakeDraftService service;

    public BusinessIntakeController(BusinessIntakeDraftService service) {
        this.service = service;
    }

    /** 解析业务描述；超时 504、模型或模板异常 502、入参非法 400。 */
    @PostMapping("/draft")
    public BusinessIntakeDraftResponse draft(@RequestBody BusinessIntakeDraftRequest request) {
        IdentityContext.require();
        if (request == null || request.text() == null || request.text().isBlank()) {
            throw new IllegalArgumentException("AI 预填内容不能为空");
        }
        try {
            return BusinessIntakeDraftResponse.from(service.draft(request.text()));
        } catch (BusinessModelTimeoutException exception) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "AI 预填响应超时，请稍后重试", exception);
        } catch (BusinessModelException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 预填暂时不可用，请稍后重试", exception);
        } catch (IllegalStateException exception) {
            // 提示词缺失或候选过多触发输入门禁，均属于服务端暂时不可用。
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 预填暂时不可用，请稍后重试", exception);
        }
    }
}
