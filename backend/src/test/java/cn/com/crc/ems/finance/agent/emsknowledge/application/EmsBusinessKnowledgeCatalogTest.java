package cn.com.crc.ems.finance.agent.emsknowledge.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmsBusinessKnowledgeCatalogTest {

    @Test
    void exposesOnlyTheApprovedNineBusinessDomains() {
        var domains = new EmsBusinessKnowledgeCatalog().list();

        assertThat(domains).hasSize(9);
        assertThat(domains).extracting(domain -> domain.code()).containsExactly("MASTER_DATA", "APPROVAL_WORKFLOW",
                "ACCOUNTING", "BUDGET", "PAYMENT", "QUALITY", "SMART_AUDIT", "MOBILE", "RUNTIME_CONFIGURATION");
        assertThat(domains.stream().flatMap(domain -> domain.schemas().stream()))
                .doesNotContain("claim", "xxljob", "arch_db");
        assertThat(domains).allSatisfy(domain -> {
            assertThat(domain.knowledgeBoundary()).contains("结构快照");
            assertThat(domain.keyTables()).isNotEmpty();
        });
    }
}
