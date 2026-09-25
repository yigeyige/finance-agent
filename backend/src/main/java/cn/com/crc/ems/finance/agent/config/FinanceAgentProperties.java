package cn.com.crc.ems.finance.agent.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Runtime configuration for the finance agent.
 *
 * <p>Sensitive values intentionally do not have a custom string representation. This
 * prevents accidental exposure when a configuration object is included in diagnostics.</p>
 */
@ConfigurationProperties(prefix = "finance-agent")
public class FinanceAgentProperties {

    private Database database = new Database();
    private DeepSeek deepSeek = new DeepSeek();
    private Workspace workspace = new Workspace();
    private Budget budget = new Budget();
    private Identity identity = new Identity();
    private Storage storage = new Storage();
    private FieldPolicy fieldPolicy = new FieldPolicy();

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public DeepSeek getDeepSeek() {
        return deepSeek;
    }

    public void setDeepSeek(DeepSeek deepSeek) {
        this.deepSeek = deepSeek;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public FieldPolicy getFieldPolicy() {
        return fieldPolicy;
    }

    public void setFieldPolicy(FieldPolicy fieldPolicy) {
        this.fieldPolicy = fieldPolicy;
    }

    public static class Database {
        private String url;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class DeepSeek {
        private String apiKey;
        private String baseUrl;
        private String model;
        private boolean stub;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public boolean isStub() {
            return stub;
        }

        public void setStub(boolean stub) {
            this.stub = stub;
        }
    }

    public static class Workspace {
        private String root;

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }
    }

    public static class Budget {
        private int maxSteps;
        private int maxTokens;
        private BigDecimal maxCost = BigDecimal.ZERO;

        public int getMaxSteps() {
            return maxSteps;
        }

        public void setMaxSteps(int maxSteps) {
            this.maxSteps = maxSteps;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        public BigDecimal getMaxCost() {
            return maxCost;
        }

        public void setMaxCost(BigDecimal maxCost) {
            this.maxCost = maxCost;
        }
    }

    public static class Identity {
        private String mode;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }

    public static class Storage {
        private String mode;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
    }

    public static class FieldPolicy {
        private boolean strict;

        public boolean isStrict() {
            return strict;
        }

        public void setStrict(boolean strict) {
            this.strict = strict;
        }
    }
}
