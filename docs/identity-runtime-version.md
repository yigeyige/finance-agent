# 身份与运行版本模型验证

DEV-DATA-002 通过 MyBatis-Plus 3.5.17 Mapper 仓储访问 `iam.agent_user`、`iam.role_binding` 和 `governance.runtime_bundle`。合成数据默认关闭，仅在 `local` 或 `test` profile 配置 `FINANCE_AGENT_IDENTITY_SEED_ENABLED=true` 时执行一次幂等种子写入；`prod` profile 不注册种子执行器。

## 数据库核对

```sql
SELECT employee_no, auth_subject, status
FROM iam.agent_user
ORDER BY employee_no;

SELECT user_id, role_code, granted_at, revoked_at
FROM iam.role_binding
ORDER BY granted_at;

SELECT runtime_bundle_id, status, application_version, agentscope_version,
       model_binding_version, prompt_bundle_version, rule_set_version,
       tool_catalog_version, source_catalog_version
FROM governance.runtime_bundle;
```

任务创建或恢复运行版本时必须使用 `RuntimeBundleRepository.findPublishedById`，该查询明确附加 `status = 'PUBLISHED'`；DRAFT、TESTED、RETIRED 版本不会被解析为可运行版本。
