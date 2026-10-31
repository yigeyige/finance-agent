package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.util.List;

/**
 *
 * @Description: EMS 导入文件转换结果，模型文件必有，其余配置仅在适用时存在。
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-10-31 12:25:00
 */
public record EmsProcessArtifactFiles(String fileName, String modelJson, String nodeParametersJson,
        String departmentApprovalsJson, String approvalRulesJson, List<String> missingItems, String importStatus) { }
