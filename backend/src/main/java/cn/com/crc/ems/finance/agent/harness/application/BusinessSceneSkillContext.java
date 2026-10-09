package cn.com.crc.ems.finance.agent.harness.application;

/** 送入模型的业务场景 Skill 受控投影，不包含管理员审计信息。 */
public record BusinessSceneSkillContext(String name, String description, String handlingType, String premiseType) {
}
