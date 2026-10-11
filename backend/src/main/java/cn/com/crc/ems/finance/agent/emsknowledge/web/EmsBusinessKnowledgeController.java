package cn.com.crc.ems.finance.agent.emsknowledge.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cn.com.crc.ems.finance.agent.emsknowledge.application.EmsBusinessKnowledgeCatalog;
import cn.com.crc.ems.finance.agent.iam.application.IdentityContext;
import cn.com.crc.ems.finance.agent.iam.domain.RoleCode;

/**
 *
 * @Description: EMS 业务域知识目录的只读 REST 入口。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-11 00:00:00
 */
@RestController
@RequestMapping("/api/v1/ems-business-knowledge")
public final class EmsBusinessKnowledgeController {

    private final EmsBusinessKnowledgeCatalog catalog;

    public EmsBusinessKnowledgeController(EmsBusinessKnowledgeCatalog catalog) {
        this.catalog = catalog;
    }

    /** 返回当前用户可读的 EMS 业务域知识目录。 */
    @GetMapping
    public List<EmsBusinessDomainKnowledgeResponse> list() {
        requireTaskRead();
        return catalog.list().stream().map(EmsBusinessDomainKnowledgeResponse::from).toList();
    }

    private static void requireTaskRead() {
        if (!IdentityContext.require().hasRole(RoleCode.OPERATOR)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "当前身份无权查看 EMS 业务知识域");
        }
    }
}
