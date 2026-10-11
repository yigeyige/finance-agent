package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 送入模型的业务场景 Skill 受控投影，不包含管理员审计信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public record BusinessSceneSkillContext(String name, String description, String handlingType, String premiseType) {
}
