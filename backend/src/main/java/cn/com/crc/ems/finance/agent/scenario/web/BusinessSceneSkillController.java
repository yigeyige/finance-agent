package cn.com.crc.ems.finance.agent.scenario.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillService;
import jakarta.validation.Valid;

/**
 *
 * @Description: 管理员维护业务场景 Skill 的 REST 入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-08 23:00:00
 */
@RestController
@RequestMapping("/api/v1/business-scene-skills")
public final class BusinessSceneSkillController {

    private final BusinessSceneSkillService skillService;

    public BusinessSceneSkillController(BusinessSceneSkillService skillService) {
        this.skillService = skillService;
    }

    /** 查询当前有效的业务场景 Skill。 */
    @GetMapping
    public List<BusinessSceneSkillResponse> list() {
        requireOperator();
        return skillService.list().stream().map(BusinessSceneSkillResponse::from).toList();
    }

    /** 新增业务场景 Skill。 */
    @PostMapping
    public ResponseEntity<BusinessSceneSkillResponse> create(@Valid @RequestBody BusinessSceneSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(BusinessSceneSkillResponse.from(skillService.create(
                request.name(), request.description(), request.handlingType(), request.premiseType(), requireOperator())));
    }

    /** 编辑业务场景 Skill。 */
    @PutMapping("/{skillId}")
    public BusinessSceneSkillResponse update(@PathVariable UUID skillId,
            @Valid @RequestBody BusinessSceneSkillRequest request) {
        return BusinessSceneSkillResponse.from(skillService.update(skillId, request.name(), request.description(),
                request.handlingType(), request.premiseType(), requireOperator()));
    }

    /** 删除业务场景 Skill，保留必要操作审计。 */
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> delete(@PathVariable UUID skillId) {
        skillService.delete(skillId, requireOperator());
        return ResponseEntity.noContent().build();
    }

    private static UUID requireOperator() {
        var identity = IdentityContext.require();
        if (!identity.hasRole(RoleCode.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权维护业务场景 Skill");
        }
        return identity.userId();
    }
}
