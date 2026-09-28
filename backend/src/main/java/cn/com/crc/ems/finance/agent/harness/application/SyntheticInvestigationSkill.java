package cn.com.crc.ems.finance.agent.harness.application;

import java.util.List;

import cn.com.crc.ems.finance.agent.shared.domain.Sha256Digest;

/**
 *
 * @Description: 不连接真实数据源的确定性合成调查 Skill 示例。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-29 10:20:00
 */
public final class SyntheticInvestigationSkill {

    public static final String SKILL_NAME = "synthetic-investigation";
    public static final String VERSION = "v1";
    public static final String SCENARIO = "configuration-drift";

    /** 执行固定场景，只返回合成发现和证据引用。 */
    public SyntheticInvestigationResult execute(String scenario) {
        if (!SCENARIO.equals(scenario)) {
            throw new IllegalArgumentException("synthetic scenario is unknown");
        }
        List<String> findings = List.of("synthetic-config-drift-detected");
        List<String> evidenceRefs = List.of("synthetic://configuration-drift/evidence-001");
        return new SyntheticInvestigationResult(SKILL_NAME, VERSION, findings, evidenceRefs,
                Sha256Digest.hex(String.join("|", findings) + "|" + String.join("|", evidenceRefs)));
    }

    public record SyntheticInvestigationResult(String skillName, String version, List<String> findings,
            List<String> evidenceRefs, String resultDigest) {
        public SyntheticInvestigationResult {
            findings = List.copyOf(findings);
            evidenceRefs = List.copyOf(evidenceRefs);
        }
    }
}
