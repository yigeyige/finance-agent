# Tasks

## 1. 修正审批配置查询提示词

- [x] 1.1 在 `DeepSeekBusinessModelClient.selectRelevantTables` 提示词补充 `t_audit_configuration` 的 `EMP_IDS=USERCODE`、`EMP_NAMES=姓名` 语义
- [x] 1.2 在 `DeepSeekBusinessModelClient.planEmsDatabaseQuery` 提示词补充审批配置查询规则（用 USERCODE/姓名匹配，不用数字 USERID 或系统角色编码）

## 2. 提高前端模型调用超时

- [x] 2.1 `model-api.ts` 的 `MODEL_TIMEOUT` 从 240_000 提高到 600_000，并同步更新注释

## 3. 验证

- [x] 3.1 后端 JDK17 编译、前端 `tsc --noEmit` 通过，jar 重新打包
- [x] 3.2 复测：guidance 返回 200，`t_audit_configuration` 证据从 0 行变为 15 行，结论与 EMS 真实数据一致；总耗时约 106s
