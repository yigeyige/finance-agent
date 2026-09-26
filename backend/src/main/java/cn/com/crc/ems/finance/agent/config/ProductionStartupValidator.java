package cn.com.crc.ems.finance.agent.config;

import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.PostConstruct;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fails fast when the production profile is configured with development-only behavior.
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

    @PostConstruct
    void validateAtStartup() {
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            validateProduction(properties);
        }
    }

    /**
     * Validates production-only requirements without exposing any configured values.
     */
    public static void validateProduction(FinanceAgentProperties properties) {
        List<String> violations = new ArrayList<>();
        FinanceAgentProperties.Database database = properties.getDatabase();
        FinanceAgentProperties.DeepSeek deepSeek = properties.getDeepSeek();
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
        if (deepSeek == null || isBlank(deepSeek.getApiKey())) {
            violations.add("finance-agent.deep-seek.api-key");
        }
        if (deepSeek == null || isBlank(deepSeek.getBaseUrl())) {
            violations.add("finance-agent.deep-seek.base-url");
        }
        if (deepSeek != null && deepSeek.isStub()) {
            violations.add("finance-agent.deep-seek.stub");
        }
        if (workspace == null || isBlank(workspace.getRoot())) {
            violations.add("finance-agent.workspace.root");
        }
        if (identity == null || identity.getMode() != FinanceAgentProperties.IdentityMode.SSO) {
            violations.add("finance-agent.identity.mode");
        }
        if (storage == null || storage.getMode() != FinanceAgentProperties.StorageMode.OBJECT_STORE) {
            violations.add("finance-agent.storage.mode");
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

}
