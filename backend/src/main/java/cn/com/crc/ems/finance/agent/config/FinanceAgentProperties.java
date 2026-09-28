package cn.com.crc.ems.finance.agent.config;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:25
 */
@ConfigurationProperties(prefix = "finance-agent")
@Validated
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
    @Valid
    private Worker worker = new Worker();

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

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
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

    public Diagnostics getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(Diagnostics diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
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
        @Min(1)
        @Max(64)
        private int connectionPoolSize = 10;

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

        public int getConnectTimeoutSeconds() {
            return connectTimeoutSeconds;
        }

        public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
        }

        public int getSocketTimeoutSeconds() {
            return socketTimeoutSeconds;
        }

        public void setSocketTimeoutSeconds(int socketTimeoutSeconds) {
            this.socketTimeoutSeconds = socketTimeoutSeconds;
        }

        public int getValidationTimeoutSeconds() {
            return validationTimeoutSeconds;
        }

        public void setValidationTimeoutSeconds(int validationTimeoutSeconds) {
            this.validationTimeoutSeconds = validationTimeoutSeconds;
        }

        public int getConnectionPoolSize() {
            return connectionPoolSize;
        }

        public void setConnectionPoolSize(int connectionPoolSize) {
            this.connectionPoolSize = connectionPoolSize;
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
        public static final String DEFAULT_BASE_URL = "https://api.deepseek.com";
        public static final String DEFAULT_MODEL = "deepseek-flash";

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

        public boolean isStub() {
            return stub;
        }

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
        @Positive
        private long maxBytes = 10 * 1024 * 1024L;
        @Positive
        private long maxFileBytes = 5 * 1024 * 1024L;
        private Set<String> allowedMediaTypes = Set.of("application/json", "text/plain", "text/csv");

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public long getMaxBytes() {
            return maxBytes;
        }

        public void setMaxBytes(long maxBytes) {
            this.maxBytes = maxBytes;
        }

        public long getMaxFileBytes() {
            return maxFileBytes;
        }

        public void setMaxFileBytes(long maxFileBytes) {
            this.maxFileBytes = maxFileBytes;
        }

        public Set<String> getAllowedMediaTypes() {
            return allowedMediaTypes;
        }

        public void setAllowedMediaTypes(Set<String> allowedMediaTypes) {
            this.allowedMediaTypes = allowedMediaTypes == null ? Set.of() : Set.copyOf(allowedMediaTypes);
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
        private boolean seedEnabled;

        public IdentityMode getMode() {
            return mode;
        }

        public void setMode(IdentityMode mode) {
            this.mode = mode;
        }

        public boolean isSeedEnabled() {
            return seedEnabled;
        }

        public void setSeedEnabled(boolean seedEnabled) {
            this.seedEnabled = seedEnabled;
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

        public StorageMode getMode() {
            return mode;
        }

        public void setMode(StorageMode mode) {
            this.mode = mode;
        }

        public String getAdapterId() {
            return adapterId;
        }

        public void setAdapterId(String adapterId) {
            this.adapterId = adapterId;
        }

        public String getTemporaryRoot() {
            return temporaryRoot;
        }

        public void setTemporaryRoot(String temporaryRoot) {
            this.temporaryRoot = temporaryRoot;
        }

        public long getMaxBytes() {
            return maxBytes;
        }

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

        public boolean isStrict() {
            return strict;
        }

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

        public boolean isApiEnabled() {
            return apiEnabled;
        }

        public void setApiEnabled(boolean apiEnabled) {
            this.apiEnabled = apiEnabled;
        }
    }

    /**
     *
     * @Description: PostgreSQL Worker 的开关、租约和有限资源配置。
     *
     * @Version: 0.1.0
     * @Author: yigeyige99
     * @Date: 2026-09-28 10:10:00
     */
    public static class Worker {
        private boolean enabled;
        @NotBlank
        private String ownerInstance = "local-worker";
        @Min(1)
        @Max(64)
        private int workerCount = 1;
        @Min(1)
        @Max(64)
        private int connectionPoolSize = 4;
        @Min(1)
        @Max(10000)
        private int queueCapacity = 16;
        @Min(10)
        @Max(600000)
        private long pollIntervalMillis = 1000;
        @Min(100)
        @Max(3600000)
        private long leaseDurationMillis = 30000;
        @Min(10)
        @Max(1800000)
        private long heartbeatIntervalMillis = 10000;
        @Min(100)
        @Max(3600000)
        private long shutdownGracePeriodMillis = 30000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getOwnerInstance() {
            return ownerInstance;
        }

        public void setOwnerInstance(String ownerInstance) {
            this.ownerInstance = ownerInstance;
        }

        public int getWorkerCount() {
            return workerCount;
        }

        public void setWorkerCount(int workerCount) {
            this.workerCount = workerCount;
        }

        public int getConnectionPoolSize() {
            return connectionPoolSize;
        }

        public void setConnectionPoolSize(int connectionPoolSize) {
            this.connectionPoolSize = connectionPoolSize;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public long getPollIntervalMillis() {
            return pollIntervalMillis;
        }

        public void setPollIntervalMillis(long pollIntervalMillis) {
            this.pollIntervalMillis = pollIntervalMillis;
        }

        public long getLeaseDurationMillis() {
            return leaseDurationMillis;
        }

        public void setLeaseDurationMillis(long leaseDurationMillis) {
            this.leaseDurationMillis = leaseDurationMillis;
        }

        public long getHeartbeatIntervalMillis() {
            return heartbeatIntervalMillis;
        }

        public void setHeartbeatIntervalMillis(long heartbeatIntervalMillis) {
            this.heartbeatIntervalMillis = heartbeatIntervalMillis;
        }

        public long getShutdownGracePeriodMillis() {
            return shutdownGracePeriodMillis;
        }

        public void setShutdownGracePeriodMillis(long shutdownGracePeriodMillis) {
            this.shutdownGracePeriodMillis = shutdownGracePeriodMillis;
        }

        @AssertTrue(message = "worker heartbeat interval must be shorter than lease duration")
        public boolean isHeartbeatBeforeLeaseExpiry() {
            return heartbeatIntervalMillis < leaseDurationMillis;
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
