package cn.com.crc.ems.finance.agent.modelconfig.application;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.com.crc.ems.finance.agent.modelconfig.domain.ModelConnectionConfig;
import cn.com.crc.ems.finance.agent.shared.domain.UtcTime;
import cn.com.crc.ems.finance.agent.shared.domain.UuidGenerator;

/**
 *
 * @Description: 平台管理员维护、更新并唯一启用当前大模型连接。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
@Service
public class ModelConnectionConfigService {
    private final ModelConnectionConfigRepository repository; private final ModelApiKeyCipher cipher; private final Clock clock;
    public ModelConnectionConfigService(ModelConnectionConfigRepository repository, ModelApiKeyCipher cipher, Clock clock) { this.repository = repository; this.cipher = cipher; this.clock = clock; }
    @Transactional(readOnly = true) public List<ModelConnectionConfig> list() { return repository.findAll(); }
    @Transactional(readOnly = true) public ModelConnectionConfig requireEnabled() { return repository.findEnabled().orElseThrow(() -> new IllegalStateException("请先在大模型配置中启用一个模型")); }
    @Transactional public ModelConnectionConfig create(String name, String provider, String apiUrl, String modelName, String reasoningEffort, String apiKey, UUID operator) { return save(null, name, provider, apiUrl, modelName, reasoningEffort, apiKey, operator); }
    @Transactional public ModelConnectionConfig update(UUID id, String name, String provider, String apiUrl, String modelName, String reasoningEffort, String apiKey, UUID operator) { return save(id, name, provider, apiUrl, modelName, reasoningEffort, apiKey, operator); }
    @Transactional public void enable(UUID id, UUID operator) { ModelConnectionConfig current = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("大模型配置不存在")); var now = UtcTime.now(clock); repository.disableAll(operator, now); repository.update(new ModelConnectionConfig(current.configId(), current.name(), current.provider(), current.apiUrl(), current.modelName(), current.reasoningEffort(), current.apiKeyCiphertext(), true, current.createdBy(), current.createdAt(), operator, now)); }
    /** 停用指定模型连接；连接与已加密密钥仍保留供管理员后续重新启用。 */
    @Transactional public void disable(UUID id, UUID operator) {
        ModelConnectionConfig current = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("大模型配置不存在"));
        if (!current.enabled()) return;
        var now = UtcTime.now(clock);
        repository.update(new ModelConnectionConfig(current.configId(), current.name(), current.provider(), current.apiUrl(),
                current.modelName(), current.reasoningEffort(), current.apiKeyCiphertext(), false, current.createdBy(),
                current.createdAt(), operator, now));
    }
    private ModelConnectionConfig save(UUID id, String name, String provider, String apiUrl, String modelName, String reasoningEffort, String apiKey, UUID operator) { var now = UtcTime.now(clock); ModelConnectionConfig old = id == null ? null : repository.findById(id).orElseThrow(() -> new IllegalArgumentException("大模型配置不存在")); if (old == null && (apiKey == null || apiKey.isBlank())) throw new IllegalArgumentException("请填写 API Key"); String ciphertext = apiKey == null || apiKey.isBlank() ? old.apiKeyCiphertext() : cipher.encrypt(apiKey); ModelConnectionConfig result = new ModelConnectionConfig(id == null ? UuidGenerator.newUuid() : id, name, provider, apiUrl, modelName, reasoningEffort, ciphertext, old != null && old.enabled(), old == null ? operator : old.createdBy(), old == null ? now : old.createdAt(), operator, now); if (old == null) repository.insert(result); else repository.update(result); return result; }
}
