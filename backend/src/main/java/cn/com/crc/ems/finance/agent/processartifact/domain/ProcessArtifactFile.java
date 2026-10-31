package cn.com.crc.ems.finance.agent.processartifact.domain;

import java.util.UUID;

/** 单个可下载的 EMS 流程文件。 */
public record ProcessArtifactFile(UUID fileId, UUID batchId, String kind, String fileName, String contentText) { }
