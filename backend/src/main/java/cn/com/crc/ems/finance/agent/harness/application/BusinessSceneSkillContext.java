package cn.com.crc.ems.finance.agent.harness.application;

/**
 *
 * @Description: 送入模型的业务场景 Skill 受控投影，不包含管理员审计信息。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-27 11:20:00
 */
public record BusinessSceneSkillContext(String name, String investigationPrompt, String decisionPacketPrompt) {
}
