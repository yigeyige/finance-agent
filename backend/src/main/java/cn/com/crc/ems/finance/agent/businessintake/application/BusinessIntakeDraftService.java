package cn.com.crc.ems.finance.agent.businessintake.application;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.com.crc.ems.finance.agent.emsdbconfig.application.EmsDatabaseConnectionConfigService;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeContext;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeDraft;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeOption;
import cn.com.crc.ems.finance.agent.harness.application.BusinessIntakeSuggestion;
import cn.com.crc.ems.finance.agent.harness.application.BusinessModelClient;
import cn.com.crc.ems.finance.agent.scenario.application.BusinessSceneSkillService;

/**
 * @Description: AI 预填领域服务：校验文本、加载候选、调用模型并按名称精确匹配；不创建任务、不访问业务数据。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-11-06 15:30:00
 */
@Service
public class BusinessIntakeDraftService {

    static final int MIN_TEXT_LENGTH = 10;
    static final int MAX_TEXT_LENGTH = 2000;
    static final int MAX_TITLE_LENGTH = 300;
    static final int MAX_DESCRIPTION_LENGTH = 8000;

    private final BusinessModelClient modelClient;
    private final BusinessSceneSkillService sceneSkillService;
    private final EmsDatabaseConnectionConfigService databaseConfigService;

    public BusinessIntakeDraftService(BusinessModelClient modelClient, BusinessSceneSkillService sceneSkillService,
            EmsDatabaseConnectionConfigService databaseConfigService) {
        this.modelClient = modelClient;
        this.sceneSkillService = sceneSkillService;
        this.databaseConfigService = databaseConfigService;
    }

    /**
     * @description: 把自由文本解析为业务处理表单字段；场景/实例按启用候选名称精确匹配。
     * @param: rawText 用户输入的业务描述
     * @return: 截断后的标题、说明与匹配结果
     * @author: yigeyige99
     */
    public BusinessIntakeDraft draft(String rawText) {
        String text = rawText == null ? "" : rawText.strip();
        if (text.length() < MIN_TEXT_LENGTH || text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException("AI 预填内容需为 10–2000 字");
        }
        List<Candidate> scenes = sceneSkillService.list().stream()
                .map(skill -> new Candidate(skill.skillId(), skill.name())).toList();
        List<Candidate> databases = databaseConfigService.listEnabled().stream()
                .map(config -> new Candidate(config.configId(), config.name())).toList();

        BusinessIntakeSuggestion suggestion = modelClient.extractBusinessIntake(new BusinessIntakeContext(text,
                scenes.stream().map(candidate -> new BusinessIntakeOption(candidate.id().toString(), candidate.name())).toList(),
                databases.stream().map(candidate -> new BusinessIntakeOption(candidate.id().toString(), candidate.name())).toList()))
                .value();

        Candidate scene = match(suggestion.sceneSkillName(), scenes);
        Candidate database = match(suggestion.databaseName(), databases);
        return new BusinessIntakeDraft(truncate(suggestion.title(), MAX_TITLE_LENGTH),
                truncate(suggestion.description(), MAX_DESCRIPTION_LENGTH),
                scene == null ? null : scene.id(), scene == null ? null : scene.name(),
                database == null ? null : database.id(), database == null ? null : database.name());
    }

    /** 名称去空格后大小写不敏感精确匹配；未命中或重名都视为匹配失败。 */
    private static Candidate match(String name, List<Candidate> candidates) {
        if (name == null || name.isBlank()) return null;
        String normalized = normalize(name);
        List<Candidate> hits = candidates.stream().filter(candidate -> normalize(candidate.name()).equals(normalized))
                .toList();
        return hits.size() == 1 ? hits.get(0) : null;
    }

    private static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }

    /** 超长时保留可读性，用省略号表示截断。 */
    private static String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1) + "…";
    }

    private record Candidate(UUID id, String name) {
    }
}
