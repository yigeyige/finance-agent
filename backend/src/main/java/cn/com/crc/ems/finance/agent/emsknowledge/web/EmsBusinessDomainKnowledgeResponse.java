package cn.com.crc.ems.finance.agent.emsknowledge.web;

import java.util.List;

import cn.com.crc.ems.finance.agent.emsknowledge.domain.EmsBusinessDomainKnowledge;

/** 业务域知识的安全只读响应。 */
public record EmsBusinessDomainKnowledgeResponse(String code, String name, String summary, List<String> schemas,
        int tableCount, List<String> keyTables, String knowledgeBoundary, String verificationHint) {

    /** 将受控领域知识投影为接口响应。 */
    public static EmsBusinessDomainKnowledgeResponse from(EmsBusinessDomainKnowledge knowledge) {
        return new EmsBusinessDomainKnowledgeResponse(knowledge.code(), knowledge.name(), knowledge.summary(),
                knowledge.schemas(), knowledge.tableCount(), knowledge.keyTables(), knowledge.knowledgeBoundary(),
                knowledge.verificationHint());
    }
}
