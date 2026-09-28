package cn.com.crc.ems.finance.agent.harness.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import cn.com.crc.ems.finance.agent.harness.domain.PlanRevision;
import cn.com.crc.ems.finance.agent.shared.domain.CanonicalJsonCodec;
import cn.com.crc.ems.finance.agent.task.application.ProposeStepsCommand;
import cn.com.crc.ems.finance.agent.task.application.StepProposalItem;

/** 将 Harness 计划修订映射为业务步骤提议，不执行步骤也不预取数据。 */
public final class HarnessPlanMapper {

    private static final String STEPS = "steps";
    private static final String GOAL = "goal";
    private static final String RECOMMENDED = "recommended";

    private HarnessPlanMapper() {
    }

    /** 只解析当前逻辑步骤为审批卡输入，最多一个推荐和两个备选。 */
    public static ProposeStepsCommand toStepProposal(PlanRevision plan, HarnessSessionBinding binding,
            UUID actorUserId, Instant occurredAt) {
        if (plan == null || binding == null || actorUserId == null || occurredAt == null) {
            throw new IllegalArgumentException("plan, binding, actorUserId and occurredAt are required");
        }
        if (!plan.sessionId().equals(binding.sessionId()) || !plan.taskId().equals(binding.taskId())
                || !plan.userId().equals(binding.userId())) {
            throw new IllegalArgumentException("plan is outside the bound session");
        }
        JsonNode root = CanonicalJsonCodec.parse(plan.planRef().json());
        if (!root.isObject() || !root.has(STEPS) || root.size() != 1 || !root.get(STEPS).isArray()
                || root.get(STEPS).isEmpty() || root.get(STEPS).size() > 3) {
            throw new IllegalArgumentException("plan must contain one to three steps");
        }
        List<StepProposalItem> items = new ArrayList<>();
        Iterator<JsonNode> steps = root.get(STEPS).elements();
        while (steps.hasNext()) {
            JsonNode item = steps.next();
            if (!item.isObject() || item.size() != 2 || !item.has(GOAL) || !item.has(RECOMMENDED)
                    || !item.get(GOAL).isTextual() || item.get(GOAL).textValue().isBlank()
                    || !item.get(RECOMMENDED).isBoolean()) {
                throw new IllegalArgumentException("plan step shape is invalid");
            }
            items.add(new StepProposalItem(item.get(GOAL).textValue(), item.get(RECOMMENDED).booleanValue()));
        }
        if (items.stream().filter(StepProposalItem::recommended).count() != 1) {
            throw new IllegalArgumentException("plan must contain exactly one recommended step");
        }
        return new ProposeStepsCommand(binding.taskId(), items, actorUserId, occurredAt);
    }
}
