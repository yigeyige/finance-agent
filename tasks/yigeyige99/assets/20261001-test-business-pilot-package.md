# P1 有限测试业务试点任务包

- 试点范围：仅 `local` / `test`，固定虚构测试业务样本。
- 数据来源：`test-business-primary`、`test-business-verification` 与 `test-business-reference-samples-v1`。
- 禁止事项：真实业务源、生产配置、真实身份、源写入、禁止字段、生产事实或实际业务影响推断。
- 运行时执行人：项目负责人；AI 只完成静态检查、编译和单元测试。

## 预检基线

| 项目 | 预期 |
|---|---:|
| 参考样本覆盖 | 4 |
| `UNKNOWN` 样本 | 1 |
| 非当前确认问题项 / 预期人工确认 | 3 |
| 当前确认样本 | 1 |

非当前确认问题项：`TST-REF-002`（未来）、`TST-REF-003`（未知）、`TST-REF-004`（无匹配）。它们不能自动转为当前确认事实。

## 人工执行顺序

1. 确认应用以 `local` profile 启动，并使用本地测试数据库；不得配置真实源或生产凭证。
2. 复核 P1 预检：参考样本版本为 `test-business-reference-samples-v1`，覆盖 4 个样本、未知 1 个、人工确认 3 次。
3. 使用测试身份人工发起测试任务，并确认范围、步骤与只读边界。
4. 执行受审批的合成调查；核对覆盖、未知/无匹配与人工确认不被自动关闭。
5. 依次复核、发布、以双角色查看并交接；记录每一步结果、任务 ID 和决策包 revision。
6. 使用现有 `scripts/qa-core-synthetic-vertical-slice.ps1` 的 `-Execute` 模式核对完整纵向流程；结果目录仅保存本地脱敏运行产物。
7. 将实际结果填入同目录的人工复核记录；未完成、失败或存在异常时同步登记问题清单，不得宣称试点通过。

## 运行时命令（由项目负责人执行）

```powershell
$repo = 'D:\Work\CR_git\emsld\finance_agent'
$evidence = Join-Path $env:TEMP 'finance-agent-p1-pilot'
powershell.exe -NoProfile -ExecutionPolicy Bypass -File `
  "$repo\scripts\qa-core-synthetic-vertical-slice.ps1" `
  -Execute `
  -BaseUrl 'http://localhost:8080' `
  -OutputDirectory $evidence
```

该命令仅验证既有 local 合成纵向切片；它不接入真实业务源，也不能替代 P2 的真实源门禁。
