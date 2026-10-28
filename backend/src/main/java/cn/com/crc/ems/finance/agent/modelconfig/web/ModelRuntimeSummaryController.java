package cn.com.crc.ems.finance.agent.modelconfig.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelRuntimeSummary;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelRuntimeSummaryService;

/**
 *
 * @Description: 面向工作台操作员的只读运行模型摘要入口，不开放模型配置维护详情。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-29 01:00:00
 */
@RestController
@RequestMapping("/api/v1/model-runtime")
public class ModelRuntimeSummaryController {
    private final ModelRuntimeSummaryService service;

    public ModelRuntimeSummaryController(ModelRuntimeSummaryService service) {
        this.service = service;
    }

    /**
     *
     * @description: 返回获准工作台身份可见的模型运行摘要。
     *
     * @return: 当前模型模式与名称
     * @throws: ResponseStatusException 当前身份无权读取摘要
     * @throws: IdentityContext.IdentityRequiredException 当前请求缺少可信身份
     * @author: yigeyige99
     */
    @GetMapping
    public ModelRuntimeSummary current() {
        if (!IdentityContext.require().hasRole(RoleCode.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权读取模型运行摘要");
        }
        return service.current();
    }
}
