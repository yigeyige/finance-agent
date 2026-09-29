# P0 证据包和门禁结论

## 实现方静态核对

在仓库根目录执行：

```powershell
pwsh -NoProfile -File scripts/qa-p0-evidence-index.ps1
```

脚本只校验证据索引、仓库内文件链接和值域，不调用网络、数据库或模型。`status=OK` 代表索引完整，不代表 P0 已签署通过。

## 三项零容忍

| 指标 | 静态基线 | 当前结论 |
| --- | ---: | --- |
| 禁止数据持久化或模型输入 | 0 | 待用户执行边界扫描 |
| 未授权审批、权限或发布 | 0 | 待用户执行 API/UI 负向场景 |
| 重复执行或重复 revision | 0 | 待用户执行中断、并发和恢复验证 |

## 用户补充证据

1. 按 `scripts/qa-data-boundary-negative-gate.ps1 -Execute` 的说明完成真实边界扫描。
2. 按 `scripts/qa-approval-permission-publication-negative-gate.ps1 -Execute` 的说明完成 API/UI 负向场景。
3. 注入中断、并发和租约恢复场景，保存 `DEV-QA-006` 证据。
4. 在本机按 `docs/deepseek-live-test.md` 执行真实 DeepSeek 验证。
5. 按 `docs/deployment-checklist.md` 构建并启动 JAR/Docker，验证外部 PostgreSQL 和健康检查。

## 签署记录模板

完成上述证据后，用户在 `scripts/fixtures/p0-evidence-index-v1.json` 的 `signOff` 中填写：

```json
{"status":"SIGNED","signedBy":"<user>","signedAt":"<ISO-8601>","conclusion":"PASS或CONDITIONAL"}
```

不得填写密钥、连接串、真实用户数据或完整模型请求响应。若任一证据缺失，结论必须保持 `CONDITIONAL`。
