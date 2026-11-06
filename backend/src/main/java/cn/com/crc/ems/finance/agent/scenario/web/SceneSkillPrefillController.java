package cn.com.crc.ems.finance.agent.scenario.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.harness.application.BusinessModelException;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelTimeoutException;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.scenario.application.SceneSkillPrefillService;

/**
 * @Description: 场景 AI 预填入口：按目标生成提示词草稿，不创建或修改场景，也不改任务编排链路。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:30:00
 */
@RestController
@RequestMapping("/api/v1/business-scene-skills")
public class SceneSkillPrefillController {

    private final SceneSkillPrefillService service;

    public SceneSkillPrefillController(SceneSkillPrefillService service) {
        this.service = service;
    }

    /** 生成单个目标的提示词草稿；超时 504、模型或模板异常 502、入参与前置缺失 400。 */
    @PostMapping("/prefill")
    public SceneSkillPrefillResponse prefill(@RequestBody SceneSkillPrefillRequest request) {
        var identity = IdentityContext.require();
        try {
            return SceneSkillPrefillResponse.from(service.prefill(request.toCommand(), identity));
        } catch (BusinessModelTimeoutException exception) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "AI 预填响应超时，请稍后重试", exception);
        } catch (BusinessModelException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 预填暂时不可用，请稍后重试", exception);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI 预填暂时不可用，请稍后重试", exception);
        }
    }
}
