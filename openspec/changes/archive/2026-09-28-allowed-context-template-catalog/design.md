# Design: AllowedContext 与模板目录

`AllowedContextFactory` 接收带 taskId 的 `ContextEvidence` 列表，每条证据必须是字段策略裁剪后的 JSON object。工厂按字段合并并拒绝冲突，输出现有 `AllowedContext`；`AllowedContext.fieldDigest()` 只摘要字段名称，模型元数据不携带上下文原文。

`PromptTemplateCatalog` 只保存 `PromptTemplateVersion`、用途、SHA-256 模板摘要和状态。`requirePublished` 同时校验 runtime bundle 为 `PUBLISHED`、prompt bundle 版本一致、用途和模板条目已发布，确保历史/草稿模板不能被模型调用。
