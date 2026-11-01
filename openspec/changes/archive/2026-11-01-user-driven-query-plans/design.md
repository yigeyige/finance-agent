## Decisions

### 1. 计划契约

```json
{
  "steps": [
    {
      "id": "process",
      "purpose": "定位流程定义",
      "sql": "SELECT ... WHERE m.NAME_ = :processName",
      "dependsOn": [],
      "bindings": {}
    },
    {
      "id": "nodes",
      "purpose": "查询节点",
      "sql": "SELECT ... WHERE procdef_id = :procdefId",
      "dependsOn": ["process"],
      "bindings": {
        "procdefId": { "stepId": "process", "column": "procdef_id" }
      }
    }
  ]
}
```

- 空 `steps` 表示本轮不查询。
- SQL 只允许 `:param` 占位符；绑定值通过 JDBC 参数注入。
- 最多 5 步，单步最多 30 行。

### 2. 模型职责

- 查询计划模型只输出 `steps`。
- 初稿模型输出 `needMoreEvidence`、`needTableReselection`，与 `reply/draft/requestedAttachments` 同级。

### 3. 执行

- 按 `dependsOn` 拓扑顺序串行执行。
- 0 行：记录 noDataTable + 合成证据，跳过依赖步骤，独立步骤继续。
- 异常/超时：记录失败合成证据，跳过依赖步骤，不自动重试。
- 绑定只允许前序步骤首行的指定列；禁止表达式和字符串拼接。

### 4. 证据与失败

- 全部步骤执行完后先保存证据快照，再调用初稿模型。
- 初稿 504/失败时保留证据，下次动作复用。
- 决策包仍只取最新快照。

### 5. 缓存与并发

- 候选/选表缓存 key：taskId + sceneId + databaseConfigId。
- `needTableReselection=true` 时失效重建。
- 同任务串行，幂等键去重；不设聊天轮次上限。

### 6. 响应与前端

- 响应增加 `evidenceRound`、`needMoreEvidence`、`needTableReselection`。
- 前端显示“第 N 轮证据”，`needMoreEvidence=true` 显示轻提示，不新增按钮。

## Risks

- 多步计划增加单轮延迟 → 限制 5 步、30 行。
- 绑定错误导致 SQL 失败 → 严格校验 stepId/column/类型。
- 旧任务数据 → 用户手动清理，不做兼容层。
