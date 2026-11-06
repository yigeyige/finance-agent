package cn.com.crc.ems.finance.agent.scenario.application;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseQueryExecutor;
import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillContext;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.SceneSkillPrefillTarget;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;

/**
 * @Description: 业务场景 AI 预填：校验目标与前置提示词、筛选表名、调用模型并按字段上限截断；不修改场景。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 17:20:00
 */
@Service
public class SceneSkillPrefillService {

    static final int MIN_TEXT_LENGTH = 10;
    static final int MAX_TEXT_LENGTH = 4000;
    static final int MAX_NAME_LENGTH = 100;
    static final int MAX_INVESTIGATION_LENGTH = 8000;
    static final int MAX_DECISION_LENGTH = 2000;
    static final int MAX_PROCESS_LENGTH = 4000;

    private final BusinessModelClient modelClient;
    private final BusinessSceneSkillService sceneSkillService;
    private final EmsDatabaseConnectionConfigService databaseConfigService;
    private final EmsDatabaseQueryExecutor queryExecutor;
    private final SceneSkillTableSelector tableSelector;

    public SceneSkillPrefillService(BusinessModelClient modelClient, BusinessSceneSkillService sceneSkillService,
            EmsDatabaseConnectionConfigService databaseConfigService, EmsDatabaseQueryExecutor queryExecutor,
            SceneSkillTableSelector tableSelector) {
        this.modelClient = modelClient;
        this.sceneSkillService = sceneSkillService;
        this.databaseConfigService = databaseConfigService;
        this.queryExecutor = queryExecutor;
        this.tableSelector = tableSelector;
    }

    /**
     * @description: 按目标生成场景提示词草稿；编辑已有场景时先校验维护权限。
     * @param: command 生成目标、描述、可选 skillId/实例与当前表单内容
     * @param: identity 当前登录身份
     * @return: 名称、内容与表名筛选统计
     * @author: yigeyige99
     */
    public SceneSkillPrefillResult prefill(SceneSkillPrefillCommand command, CurrentIdentity identity) {
        if (command.target() == null) throw new IllegalArgumentException("生成目标不合法");
        String text = command.text() == null ? "" : command.text().strip();
        if (text.length() < MIN_TEXT_LENGTH || text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException("AI 预填内容需为 10–4000 字");
        }
        if (command.skillId() != null) {
            sceneSkillService.requireMaintainer(command.skillId(), identity);
        }
        String prerequisite = prerequisite(command);
        SceneSkillTableSelector.TableSelection selection = selectTables(command.databaseConfigId(), text,
                command.target() == SceneSkillPrefillTarget.INVESTIGATION);

        SceneSkillPrefillSuggestion suggestion = modelClient.extractSceneSkillPrefill(new SceneSkillPrefillContext(
                command.target(), text, command.sceneName(), prerequisite,
                command.target() == SceneSkillPrefillTarget.INVESTIGATION ? selection.tables() : List.of())).value();

        String name = command.target() == SceneSkillPrefillTarget.INVESTIGATION
                ? truncate(suggestion.name(), MAX_NAME_LENGTH) : "";
        String content = truncate(suggestion.content(), contentLimit(command.target()));
        String warning = command.databaseConfigId() != null && selection.tables().isEmpty()
                ? "本次未使用表结构（筛选后无可用表）" : null;
        return new SceneSkillPrefillResult(command.target(), name, content, selection.tables().size(),
                selection.excludedCount(), warning);
    }

    /** 决策包依赖调查提示词，流程文件依赖决策包提示词；缺失时直接阻断，不调用模型。 */
    private static String prerequisite(SceneSkillPrefillCommand command) {
        return switch (command.target()) {
            case INVESTIGATION -> "";
            case DECISION_PACKET -> requirePrompt(command.investigationPrompt(), "请先生成或填写调查提示词");
            case PROCESS_ARTIFACT -> requirePrompt(command.decisionPacketPrompt(), "请先生成或填写决策包提示词");
        };
    }

    private static String requirePrompt(String prompt, String message) {
        if (prompt == null || prompt.isBlank()) throw new IllegalArgumentException(message);
        return prompt.strip();
    }

    /** 只有调查目标需要表名；决策包与流程文件只用前置提示词，避免无关表名干扰。 */
    private SceneSkillTableSelector.TableSelection selectTables(java.util.UUID databaseConfigId, String text,
            boolean includeTables) {
        if (!includeTables || databaseConfigId == null) {
            return new SceneSkillTableSelector.TableSelection(List.of(), 0, List.of());
        }
        EmsDatabaseConnectionConfig config = databaseConfigService.resolveEnabled(databaseConfigId);
        List<String> tables = queryExecutor.listAllowedTables(config).stream()
                .map(table -> table.schema() + "." + table.table()).toList();
        return tableSelector.select(tables, text);
    }

    private static int contentLimit(SceneSkillPrefillTarget target) {
        return switch (target) {
            case INVESTIGATION -> MAX_INVESTIGATION_LENGTH;
            case DECISION_PACKET -> MAX_DECISION_LENGTH;
            case PROCESS_ARTIFACT -> MAX_PROCESS_LENGTH;
        };
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1) + "…";
    }
}
