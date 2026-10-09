package cn.com.crc.ems.finance.agent.config;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import jakarta.annotation.PostConstruct;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:24
 */
@Component
public class ProductionStartupValidator {

    private final Environment environment;
    private final FinanceAgentProperties properties;

    @Autowired
    public ProductionStartupValidator(Environment environment, FinanceAgentProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @author: yigeyige99
     */
    @PostConstruct
    void validateAtStartup() {
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            validateProduction(properties);
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: properties 输入参数。
     * @author: yigeyige99
     */
    public static void validateProduction(FinanceAgentProperties properties) {
        List<String> violations = new ArrayList<>();
        FinanceAgentProperties.Database database = properties.getDatabase();
        FinanceAgentProperties.DeepSeek deepSeek = properties.getDeepSeek();
        FinanceAgentProperties.Security security = properties.getSecurity();
        FinanceAgentProperties.Source source = properties.getSource();
        FinanceAgentProperties.Workspace workspace = properties.getWorkspace();
        FinanceAgentProperties.Identity identity = properties.getIdentity();
        FinanceAgentProperties.Storage storage = properties.getStorage();
        FinanceAgentProperties.FieldPolicy fieldPolicy = properties.getFieldPolicy();

        if (database == null || isBlank(database.getUrl())) {
            violations.add("finance-agent.database.url");
        }
        if (database == null || isBlank(database.getUsername())) {
            violations.add("finance-agent.database.username");
        }
        if (database == null || isBlank(database.getPassword())) {
            violations.add("finance-agent.database.password");
        }
        if (deepSeek != null && deepSeek.isStub()) {
            violations.add("finance-agent.deep-seek.stub");
        }
        if (security == null || isBlank(security.getModelSecretKey()) || security.getModelSecretKey().length() < 32) {
            violations.add("finance-agent.security.model-secret-key");
        }
        if (workspace == null || isBlank(workspace.getRoot())) {
            violations.add("finance-agent.workspace.root");
        } else if (!Path.of(workspace.getRoot()).isAbsolute()) {
            violations.add("finance-agent.workspace.root.absolute");
        }
        if (workspace == null || workspace.getMaxBytes() <= 0) {
            violations.add("finance-agent.workspace.max-bytes");
        }
        if (workspace == null || workspace.getMaxFileBytes() <= 0
                || (workspace.getMaxBytes() > 0 && workspace.getMaxFileBytes() > workspace.getMaxBytes())) {
            violations.add("finance-agent.workspace.max-file-bytes");
        }
        if (workspace == null || workspace.getAllowedMediaTypes() == null
                || workspace.getAllowedMediaTypes().isEmpty()) {
            violations.add("finance-agent.workspace.allowed-media-types");
        }
        if (identity == null || identity.getMode() != FinanceAgentProperties.IdentityMode.SSO) {
            violations.add("finance-agent.identity.mode");
        }
        if (identity != null && identity.isSeedEnabled()) {
            violations.add("finance-agent.identity.seed-enabled");
        }
        if (storage == null || storage.getMode() != FinanceAgentProperties.StorageMode.OBJECT_STORE) {
            violations.add("finance-agent.storage.mode");
        }
        if (storage == null || isBlank(storage.getAdapterId()) || isDevelopmentStorageAdapter(storage.getAdapterId())) {
            violations.add("finance-agent.storage.adapter-id");
        }
        if (source == null || source.isStub()) {
            violations.add("finance-agent.source.stub");
        }
        if (fieldPolicy == null || !fieldPolicy.isStrict()) {
            violations.add("finance-agent.field-policy.strict");
        }

        if (!violations.isEmpty()) {
            throw new IllegalStateException("Invalid production configuration: " + String.join(", ", violations));
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean isDevelopmentStorageAdapter(String value) {
        return "none".equalsIgnoreCase(value) || "temporary".equalsIgnoreCase(value)
                || "local".equalsIgnoreCase(value) || "memory".equalsIgnoreCase(value);
    }

}
