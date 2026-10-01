# 测试业务只读源适配器技术验收记录

## 范围

- 任务：`DEV-P1-004`。
- 变更：`add-test-business-readonly-source-adapter`。
- 环境边界：仅 local/test；固定离线虚构样本；无网络、无真实凭证、无生产数据。

## 已核对交付物

| 项目 | 结果 |
|---|---|
| SourceCatalog | `test-business-catalog-v1`，模板 `test.personnel.assignment.v1`，参数白名单为 `page`、`size` |
| 来源版本 | `test-business-fixture-v1` |
| 字段投影 | 仅测试人员标识、测试角色、测试组织范围与生效状态 |
| 只读边界 | 不提供写操作、任意 SQL、任意 URL、网络客户端或真实凭证 |
| 失败关闭 | 未登记参数、目录漂移、fixture 不可用与 prod 调用均返回稳定失败语义 |

## TCK 与静态验证

- `TestBusinessSourceAdapterTest`：通过稳定分页、字段白名单、非法参数、目录漂移、fixture 不可用和 profile 隔离检查。
- `backend/mvn -q test`：282 tests，0 failures，0 errors。
- `openspec validate add-test-business-readonly-source-adapter --strict`：通过。

## 结论

适配器仅满足 P1 测试业务源技术范围，不构成真实源、生产认证、真实字段授权或生产发布的批准依据。
