package cn.com.crc.ems.finance.agent.harness.application;

import java.util.Optional;

/**
 *
 * @Description: 提示词模板的持久化端口，按编码读取模板正文。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-23 18:00:00
 */
public interface PromptTemplateRepository {

    /** 按模板编码读取正文，不存在时返回空。 */
    Optional<PromptTemplate> findByCode(String templateCode);
}
