package cn.com.crc.ems.finance.agent.emsknowledge.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.com.crc.ems.finance.agent.emsknowledge.application.EmsBusinessKnowledgeCatalog;
import cn.com.crc.ems.finance.agent.iam.application.CurrentIdentity;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

class EmsBusinessKnowledgeControllerTest {

    @AfterEach
    void clearIdentity() {
        IdentityContext.clear();
    }

    @Test
    void returnsTheCuratedCatalogForAnOperator() throws Exception {
        IdentityContext.set(new CurrentIdentity(UUID.randomUUID(), "operator", "操作员", Set.of(RoleCode.OPERATOR)));
        var mvc = MockMvcBuilders.standaloneSetup(new EmsBusinessKnowledgeController(new EmsBusinessKnowledgeCatalog()))
                .build();

        mvc.perform(get("/api/v1/ems-business-knowledge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(9))
                .andExpect(jsonPath("$[0].code").value("MASTER_DATA"))
                .andExpect(jsonPath("$[0].knowledgeBoundary").value(org.hamcrest.Matchers.containsString("结构快照")));
    }
}
