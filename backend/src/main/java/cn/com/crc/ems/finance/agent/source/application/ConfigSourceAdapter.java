package cn.com.crc.ems.finance.agent.source.application;

/**
 *
 * @Description: 只读配置源适配器端口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 21:28:41
 */
public interface ConfigSourceAdapter {

    SourceCatalog catalog();

    SourcePage query(SourceQuery query, SyntheticScenario scenario, long seed);

    ScopePage<java.util.Map<String, Object>> query(ApprovedQuery query);
}
