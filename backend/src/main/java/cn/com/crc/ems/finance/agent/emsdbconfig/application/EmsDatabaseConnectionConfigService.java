package cn.com.crc.ems.finance.agent.emsdbconfig.application;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.crc.ems.finance.agent.emsdbconfig.domain.EmsDatabaseConnectionConfig;
import cn.com.crc.ems.finance.agent.modelconfig.application.ModelApiKeyCipher;
import cn.com.crc.ems.finance.agent.shared.domain.UtcTime;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/** 平台管理员维护唯一启用的 EMS MySQL 只读连接。 */
@Service
public class EmsDatabaseConnectionConfigService {
    private final EmsDatabaseConnectionConfigRepository repository;
    private final ModelApiKeyCipher cipher;
    private final EmsDatabaseQueryExecutor executor;
    private final Clock clock;

    public EmsDatabaseConnectionConfigService(EmsDatabaseConnectionConfigRepository repository, ModelApiKeyCipher cipher,
            EmsDatabaseQueryExecutor executor, Clock clock) {
        this.repository = repository;
        this.cipher = cipher;
        this.executor = executor;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<EmsDatabaseConnectionConfig> list() { return repository.findAll(); }

    @Transactional(readOnly = true)
    public EmsDatabaseConnectionConfig requireEnabled() {
        return repository.findEnabled().orElseThrow(() -> new IllegalStateException("请先在 EMS 数据库配置中启用一个连接"));
    }

    @Transactional
    public EmsDatabaseConnectionConfig create(String name, String jdbcUrl, String username, String password,
            int connectTimeoutSeconds, int queryTimeoutSeconds, int maxRows, UUID operator) {
        return save(null, name, jdbcUrl, username, password, connectTimeoutSeconds, queryTimeoutSeconds, maxRows, operator);
    }

    @Transactional
    public EmsDatabaseConnectionConfig update(UUID configId, String name, String jdbcUrl, String username, String password,
            int connectTimeoutSeconds, int queryTimeoutSeconds, int maxRows, UUID operator) {
        return save(configId, name, jdbcUrl, username, password, connectTimeoutSeconds, queryTimeoutSeconds, maxRows, operator);
    }

    @Transactional
    public void enable(UUID configId, UUID operator) {
        EmsDatabaseConnectionConfig current = repository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("EMS 数据库配置不存在"));
        var now = UtcTime.now(clock);
        repository.disableAll(operator, now);
        repository.update(copy(current, true, operator, now));
    }

    @Transactional
    public void disable(UUID configId, UUID operator) {
        EmsDatabaseConnectionConfig current = repository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("EMS 数据库配置不存在"));
        if (current.enabled()) {
            repository.update(copy(current, false, operator, UtcTime.now(clock)));
        }
    }

    /** 只通过固定探针验证连通性；不返回 URL、账号或 JDBC 详情。 */
    @Transactional(readOnly = true)
    public boolean test(UUID configId) {
        return repository.findById(configId).map(executor::probe).orElseThrow(() -> new IllegalArgumentException("EMS 数据库配置不存在"));
    }

    private EmsDatabaseConnectionConfig save(UUID configId, String name, String jdbcUrl, String username, String password,
            int connectTimeoutSeconds, int queryTimeoutSeconds, int maxRows, UUID operator) {
        EmsDatabaseConnectionConfig old = configId == null ? null : repository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("EMS 数据库配置不存在"));
        if (old == null && (password == null || password.isBlank())) {
            throw new IllegalArgumentException("请填写数据库密码");
        }
        var now = UtcTime.now(clock);
        String ciphertext = password == null || password.isBlank() ? old.passwordCiphertext() : cipher.encrypt(password);
        EmsDatabaseConnectionConfig result = new EmsDatabaseConnectionConfig(configId == null ? UuidGenerator.newUuid() : configId,
                name, jdbcUrl, username, ciphertext, connectTimeoutSeconds, queryTimeoutSeconds, maxRows,
                old != null && old.enabled(), old == null ? operator : old.createdBy(), old == null ? now : old.createdAt(), operator, now);
        if (old == null) repository.insert(result); else repository.update(result);
        return result;
    }

    private static EmsDatabaseConnectionConfig copy(EmsDatabaseConnectionConfig value, boolean enabled, UUID operator,
            java.time.Instant updatedAt) {
        return new EmsDatabaseConnectionConfig(value.configId(), value.name(), value.jdbcUrl(), value.username(),
                value.passwordCiphertext(), value.connectTimeoutSeconds(), value.queryTimeoutSeconds(), value.maxRows(), enabled,
                value.createdBy(), value.createdAt(), operator, updatedAt);
    }
}
