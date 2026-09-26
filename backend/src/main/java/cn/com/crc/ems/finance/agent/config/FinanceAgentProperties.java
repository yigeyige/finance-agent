package cn.com.crc.ems.finance.agent.config;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "finance-agent")
@Validated
/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:25
 */
public class FinanceAgentProperties {

    @Valid
    private Database database = new Database();
    @Valid
    private DeepSeek deepSeek = new DeepSeek();
    @Valid
    private Source source = new Source();
    @Valid
    private Workspace workspace = new Workspace();
    @Valid
    private Budget budget = new Budget();
    @Valid
    private Identity identity = new Identity();
    @Valid
    private Storage storage = new Storage();
    @Valid
    private FieldPolicy fieldPolicy = new FieldPolicy();
    @Valid
    private Diagnostics diagnostics = new Diagnostics();

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Database getDatabase() {
        return database;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: database 输入参数。
     * @author: yigeyige99
     */
    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public DeepSeek getDeepSeek() {
        return deepSeek;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: deepSeek 输入参数。
     * @author: yigeyige99
     */
    public void setDeepSeek(DeepSeek deepSeek) {
        this.deepSeek = deepSeek;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Source getSource() {
        return source;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: source 输入参数。
     * @author: yigeyige99
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: workspace 输入参数。
     * @author: yigeyige99
     */
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Budget getBudget() {
        return budget;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: budget 输入参数。
     * @author: yigeyige99
     */
    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Identity getIdentity() {
        return identity;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: identity 输入参数。
     * @author: yigeyige99
     */
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: storage 输入参数。
     * @author: yigeyige99
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public FieldPolicy getFieldPolicy() {
        return fieldPolicy;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: fieldPolicy 输入参数。
     * @author: yigeyige99
     */
    public void setFieldPolicy(FieldPolicy fieldPolicy) {
        this.fieldPolicy = fieldPolicy;
    }

    /**
     *
     * @description: 读取当前配置或状态值。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    /**
     *
     * @description: 更新当前配置或状态值。
     *
     * @param: diagnostics 输入参数。
     * @author: yigeyige99
     */
    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Database {
        private String url;
        private String username;
        private String password;
        @Positive
        private int connectTimeoutSeconds = 2;
        @Positive
        private int socketTimeoutSeconds = 2;
        @Positive
        private int validationTimeoutSeconds = 2;

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getUrl() {
            return url;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: url 输入参数。
         * @author: yigeyige99
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getUsername() {
            return username;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: username 输入参数。
         * @author: yigeyige99
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getPassword() {
            return password;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: password 输入参数。
         * @author: yigeyige99
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public int getConnectTimeoutSeconds() {
            return connectTimeoutSeconds;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: connectTimeoutSeconds 输入参数。
         * @author: yigeyige99
         */
        public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public int getSocketTimeoutSeconds() {
            return socketTimeoutSeconds;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: socketTimeoutSeconds 输入参数。
         * @author: yigeyige99
         */
        public void setSocketTimeoutSeconds(int socketTimeoutSeconds) {
            this.socketTimeoutSeconds = socketTimeoutSeconds;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public int getValidationTimeoutSeconds() {
            return validationTimeoutSeconds;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: validationTimeoutSeconds 输入参数。
         * @author: yigeyige99
         */
        public void setValidationTimeoutSeconds(int validationTimeoutSeconds) {
            this.validationTimeoutSeconds = validationTimeoutSeconds;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class DeepSeek {
        private String apiKey;
        private String baseUrl;
        private String model;
        private boolean stub;

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getApiKey() {
            return apiKey;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: apiKey 输入参数。
         * @author: yigeyige99
         */
        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getBaseUrl() {
            return baseUrl;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: baseUrl 输入参数。
         * @author: yigeyige99
         */
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getModel() {
            return model;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: model 输入参数。
         * @author: yigeyige99
         */
        public void setModel(String model) {
            this.model = model;
        }

        /**
         *
         * @description: 判断当前配置或状态是否满足条件。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public boolean isStub() {
            return stub;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: stub 输入参数。
         * @author: yigeyige99
         */
        public void setStub(boolean stub) {
            this.stub = stub;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Source {
        private boolean stub;

        /**
         *
         * @description: 判断当前配置或状态是否满足条件。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public boolean isStub() {
            return stub;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: stub 输入参数。
         * @author: yigeyige99
         */
        public void setStub(boolean stub) {
            this.stub = stub;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Workspace {
        private String root;

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getRoot() {
            return root;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: root 输入参数。
         * @author: yigeyige99
         */
        public void setRoot(String root) {
            this.root = root;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Budget {
        @Positive
        private int maxSteps;
        @Positive
        private int maxTokens;
        @DecimalMin("0.0")
        private BigDecimal maxCost = BigDecimal.ZERO;

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public int getMaxSteps() {
            return maxSteps;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: maxSteps 输入参数。
         * @author: yigeyige99
         */
        public void setMaxSteps(int maxSteps) {
            this.maxSteps = maxSteps;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public int getMaxTokens() {
            return maxTokens;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: maxTokens 输入参数。
         * @author: yigeyige99
         */
        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public BigDecimal getMaxCost() {
            return maxCost;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: maxCost 输入参数。
         * @author: yigeyige99
         */
        public void setMaxCost(BigDecimal maxCost) {
            this.maxCost = maxCost;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Identity {
        private IdentityMode mode;

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public IdentityMode getMode() {
            return mode;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: mode 输入参数。
         * @author: yigeyige99
         */
        public void setMode(IdentityMode mode) {
            this.mode = mode;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Storage {
        private StorageMode mode;
        private String adapterId;
        private String temporaryRoot;
        @Positive
        private long maxBytes = 10 * 1024 * 1024L;

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public StorageMode getMode() {
            return mode;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: mode 输入参数。
         * @author: yigeyige99
         */
        public void setMode(StorageMode mode) {
            this.mode = mode;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getAdapterId() {
            return adapterId;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: adapterId 输入参数。
         * @author: yigeyige99
         */
        public void setAdapterId(String adapterId) {
            this.adapterId = adapterId;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public String getTemporaryRoot() {
            return temporaryRoot;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: temporaryRoot 输入参数。
         * @author: yigeyige99
         */
        public void setTemporaryRoot(String temporaryRoot) {
            this.temporaryRoot = temporaryRoot;
        }

        /**
         *
         * @description: 读取当前配置或状态值。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public long getMaxBytes() {
            return maxBytes;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: maxBytes 输入参数。
         * @author: yigeyige99
         */
        public void setMaxBytes(long maxBytes) {
            this.maxBytes = maxBytes;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class FieldPolicy {
        private boolean strict;

        /**
         *
         * @description: 判断当前配置或状态是否满足条件。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public boolean isStrict() {
            return strict;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: strict 输入参数。
         * @author: yigeyige99
         */
        public void setStrict(boolean strict) {
            this.strict = strict;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public static class Diagnostics {
        private boolean apiEnabled;

        /**
         *
         * @description: 判断当前配置或状态是否满足条件。
         *
         * @return: 当前处理结果。
         * @author: yigeyige99
         */
        public boolean isApiEnabled() {
            return apiEnabled;
        }

        /**
         *
         * @description: 更新当前配置或状态值。
         *
         * @param: apiEnabled 输入参数。
         * @author: yigeyige99
         */
        public void setApiEnabled(boolean apiEnabled) {
            this.apiEnabled = apiEnabled;
        }
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public enum IdentityMode {
        TEST,
        STUB,
        SSO
    }

    /**
     *
     * @Description: 当前类型承载本模块的职责、边界与协作契约。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-26 19:35:25
     */
    public enum StorageMode {
        LOCAL,
        MEMORY,
        OBJECT_STORE
    }
}
