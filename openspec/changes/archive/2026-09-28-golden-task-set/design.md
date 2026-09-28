# Design

- 合成源继续使用固定 synthetic-config-v1，所有场景由 SyntheticScenario 枚举驱动；固定 seed 17 保证记录顺序和摘要可复现。
- COMPLETE_NO_MATCH 返回空结果，PARTIAL_NO_MATCH 返回带 UNKNOWN 分类的有限结果，PAGINATION_FAILURE 在第二页返回稳定错误码。
- 黄金 manifest 放在测试资源中，使用独立 schema 描述字段，测试逐项校验场景覆盖、packet 必备章节、版本绑定、相同 seed 稳定性及敏感词禁入。
- manifest 只保存预期摘要和结构，不保存真实人员、资金、凭证或源系统原文。
