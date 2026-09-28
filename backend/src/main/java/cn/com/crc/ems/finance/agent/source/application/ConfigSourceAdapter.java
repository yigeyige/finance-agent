package cn.com.crc.ems.finance.agent.source.application;

/** 只读配置源适配器端口。 */
public interface ConfigSourceAdapter {

    SourceCatalog catalog();

    SourcePage query(SourceQuery query, SyntheticScenario scenario, long seed);
}
