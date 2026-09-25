# 附件 B：接口、工具与适配器契约

> 所属主方案：[财务共享智能运维 Agent 生产落地技术方案](00-财务共享智能运维Agent-生产落地技术方案.md)
> 定位：定义前后端 API、Agent 工具、源系统适配器、模型网关和结构化输出的最小生产契约。示例字段只属于 Agent 自有契约；源系统真实对象和字段均以 `TBC-SRC-*` 标识。

## 1. 契约原则

1. REST API 使用 `/api/v1`，请求必须携带 `Idempotency-Key`（查询 GET 除外）和链路 ID。
2. 身份从统一认证 token 得出，不接受请求体传入操作者身份。
3. 所有改变业务状态的请求使用乐观版本 `If-Match` 或 `expectedRowVersion`。
4. 所有 Agent 工具必须注册静态 JSON Schema；模型不能拼接 SQL、URL 或源字段名。
5. 源适配器只接受 `queryTemplateId + typed parameters`，不能接受任意 SQL。
6. 错误响应区分可重试、需人工重新批准、权限拒绝、契约漂移和数据不完整。
7. 契约日志只保存 ID、版本、状态、耗时和摘要，不保存被裁剪字段、凭证或完整 prompt/response。

## 2. API 资源

| 资源 | 说明 |
|---|---|
| `/tasks` | 调查任务、状态和版本 |
| `/tasks/{id}/scope-revisions` | 不可变范围版本 |
| `/tasks/{id}/premises` | 运维人员提供的任务前提及来源 |
| `/tasks/{id}/steps` | Agent 提议的逻辑调查步骤 |
| `/tasks/{id}/step-approvals` | 步骤批准卡 |
| `/tasks/{id}/findings` | 事实、条件性风险和未知项 |
| `/tasks/{id}/professional-confirmations` | 逐项专业判断 |
| `/tasks/{id}/packet-drafts` | 决策包草稿 |
| `/tasks/{id}/reviews` | 准确草稿复核 |
| `/tasks/{id}/publish` | 发布前全范围复核和原子发布 |
| `/published-packets/{revisionId}` | OPERATOR/VIEWER 共用的已发布视图 |
| `/knowledge/documents` | 管理员受控导入资料 |
| `/admin/runtime-bundles` | 版本组合的测试和发布 |

## 3. OpenAPI 关键片段

```yaml
openapi: 3.1.0
info:
  title: Finance Operations Agent API
  version: 1.0.0
paths:
  /api/v1/tasks:
    post:
      operationId: createTask
      security: [{ unifiedAuth: [OPERATOR] }]
      parameters:
        - $ref: '#/components/parameters/IdempotencyKey'
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/CreateTaskRequest' }
      responses:
        '201':
          description: Task created in DRAFT state
          content:
            application/json:
              schema: { $ref: '#/components/schemas/TaskView' }
        '409': { $ref: '#/components/responses/Conflict' }
  /api/v1/tasks/{taskId}/step-approvals:
    post:
      operationId: approveStep
      security: [{ unifiedAuth: [OPERATOR] }]
      parameters:
        - $ref: '#/components/parameters/IdempotencyKey'
        - name: taskId
          in: path
          required: true
          schema: { type: string, format: uuid }
      requestBody:
        required: true
        content:
          application/json:
            schema: { $ref: '#/components/schemas/StepApprovalRequest' }
      responses:
        '201': { description: Approval revision created }
        '409': { description: Task/step/scope version is stale }
        '422': { description: Approval binding is incomplete }
  /api/v1/tasks/{taskId}/publish:
    post:
      operationId: publishPacket
      security: [{ unifiedAuth: [OPERATOR] }]
      parameters:
        - $ref: '#/components/parameters/IdempotencyKey'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              additionalProperties: false
              required: [draftId, reviewedContentDigest, reviewId, expectedRowVersion]
              properties:
                draftId: { type: string, format: uuid }
                reviewedContentDigest: { type: string, pattern: '^[a-f0-9]{64}$' }
                reviewId: { type: string, format: uuid }
                expectedRowVersion: { type: integer, minimum: 0 }
      responses:
        '201': { description: Immutable revision published }
        '409': { description: Scope changed, draft changed, or duplicate command }
        '412': { description: Prepublication recheck is not publishable }
components:
  securitySchemes:
    unifiedAuth:
      type: openIdConnect
      openIdConnectUrl: TBC-AUTH-01
  parameters:
    IdempotencyKey:
      name: Idempotency-Key
      in: header
      required: true
      schema: { type: string, minLength: 16, maxLength: 200 }
  schemas:
    CreateTaskRequest:
      type: object
      additionalProperties: false
      required: [title, originalQuestion, premises]
      properties:
        title: { type: string, minLength: 1, maxLength: 300 }
        originalQuestion: { type: string, minLength: 1, maxLength: 8000 }
        premises:
          type: array
          maxItems: 20
          items: { $ref: '#/components/schemas/PremiseInput' }
    PremiseInput:
      type: object
      additionalProperties: false
      required: [type, subjectKey, statement, sourceDescription]
      properties:
        type: { type: string, maxLength: 50 }
        subjectKey: { type: string, maxLength: 200 }
        statement: { type: string, maxLength: 2000 }
        sourceDescription: { type: string, maxLength: 2000 }
    StepApprovalRequest:
      type: object
      additionalProperties: false
      required: [stepId, goalHash, sourceBindingId, scopeRevisionId,
                 fieldPolicyVersion, queryTemplateVersion, maxCalls, expectedRowVersion]
      properties:
        stepId: { type: string, format: uuid }
        goalHash: { type: string, pattern: '^[a-f0-9]{64}$' }
        sourceBindingId: { type: string, format: uuid }
        scopeRevisionId: { type: string, format: uuid }
        fieldPolicyVersion: { type: string, maxLength: 80 }
        queryTemplateVersion: { type: string, maxLength: 80 }
        maxCalls: { type: integer, minimum: 1, maximum: 100 }
        expiresAt: { type: string, format: date-time }
        expectedRowVersion: { type: integer, minimum: 0 }
```

## 4. 标准错误模型

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "object",
  "additionalProperties": false,
  "required": ["code", "message", "traceId", "retryClass"],
  "properties": {
    "code": {"type": "string", "pattern": "^[A-Z0-9_]{3,80}$"},
    "message": {"type": "string", "maxLength": 1000},
    "traceId": {"type": "string", "maxLength": 100},
    "retryClass": {
      "enum": ["NOT_RETRYABLE", "SAME_APPROVAL_RETRY", "NEW_APPROVAL_REQUIRED", "MANUAL_RECONCILIATION"]
    },
    "details": {"type": "object", "additionalProperties": true}
  }
}
```

| 错误码 | 含义 | 行为 |
|---|---|---|
| `APPROVAL_BINDING_MISMATCH` | 范围/字段/模板/来源不一致 | 新审批 |
| `APPROVAL_EXPIRED` | 审批过期或被撤销 | 新审批 |
| `FIELD_READ_FORBIDDEN` | 通道将暴露禁止读取字段 | 失败关闭，整改来源 |
| `SOURCE_CONTRACT_DRIFT` | 返回结构与已发布契约不符 | 暂停来源，人工确认 |
| `SOURCE_RESULT_UNCERTAIN` | 超时后结果状态不明 | 人工对账，不盲重试 |
| `COVERAGE_INCOMPLETE` | 分页/关系路径不完整 | 允许部分结果，禁止全范围否定 |
| `DRAFT_STALE` | 复核后事实/时间分类/版本改变 | 新草稿和新复核 |
| `MODEL_SCHEMA_INVALID` | 模型输出未通过 JSON Schema | 限次纠正后转人工 |
| `MODEL_UNAVAILABLE` | 单模型不可用 | 暂停模型步骤 |

## 5. 工具目录契约

### 5.1 工具元数据

每个工具注册项包含：

```json
{
  "toolCode": "source.query.references",
  "toolVersion": "1.0.0",
  "riskClass": "READ_CONFIG",
  "inputSchemaRef": "schema://tool/source-query-references/1.0.0",
  "outputSchemaRef": "schema://tool/source-query-result/1.0.0",
  "requiredTaskStates": ["RUNNING_STEP"],
  "requiresStepApproval": true,
  "supportsIdempotency": true,
  "maxResultBytes": 1048576,
  "timeoutPolicyId": "TBC-SRC-07",
  "enabledEnvironments": ["TEST", "PROD"]
}
```

工具目录经管理员审核发布并进入 `runtime_bundle`。运行时不能由模型添加 URL、MCP 服务或新工具。

### 5.2 首期工具

| 工具代码 | 用途 | 输入 | 输出 |
|---|---|---|---|
| `source.enumerate.scope` | 枚举批准范围 | scope、template、page token | 对象键、分页、版本/水位、覆盖元数据 |
| `source.query.references` | 查询对象配置引用 | 对象键、关系类型、批准字段 | 已裁剪配置行、版本和 checked_at |
| `source.recheck.scope` | 发布前重查 | 范围版本、旧摘要 | 新摘要、增删改、分类变化 |
| `rules.evaluate` | 确定性规则计算 | 快照 ID、规则版本 | 结果、输入/输出摘要、见证 ID |
| `rules.recompute` | 独立复算 | 见证 ID | MATCHED/MISMATCHED/UNAVAILABLE |
| `knowledge.search` | 检索获准知识 | 查询、范围、发布版本 | 文档片段及引用，不返回事实确认 |
| `packet.validate` | 决策包结构和依据校验 | 草稿 ID | 错误、警告、可发布性 |

### 5.3 源查询输入

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "schema://tool/source-query-references/1.0.0",
  "type": "object",
  "additionalProperties": false,
  "required": ["taskId", "stepId", "approvalId", "sourceBindingId",
               "queryTemplateVersion", "scopeRevisionId", "parameters", "requestedFields"],
  "properties": {
    "taskId": {"type": "string", "format": "uuid"},
    "stepId": {"type": "string", "format": "uuid"},
    "approvalId": {"type": "string", "format": "uuid"},
    "sourceBindingId": {"type": "string", "format": "uuid"},
    "queryTemplateVersion": {"type": "string"},
    "scopeRevisionId": {"type": "string", "format": "uuid"},
    "parameters": {"type": "object", "additionalProperties": false},
    "requestedFields": {
      "type": "array", "uniqueItems": true,
      "items": {"type": "string", "pattern": "^[A-Za-z0-9_.-]{1,100}$"}
    },
    "pageToken": {"type": "string", "maxLength": 2000}
  }
}
```

`parameters` 的具体属性由发布的查询模板 schema 决定，不能在通用 schema 中放开任意键。

### 5.4 源查询输出

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "schema://tool/source-query-result/1.0.0",
  "type": "object",
  "additionalProperties": false,
  "required": ["checkedAt", "records", "coverage", "resultDigest"],
  "properties": {
    "checkedAt": {"type": "string", "format": "date-time"},
    "sourceVersion": {"type": ["string", "null"]},
    "records": {
      "type": "array",
      "items": {
        "type": "object",
        "additionalProperties": false,
        "required": ["objectType", "objectKey", "allowedFields", "effectiveClass", "recordDigest"],
        "properties": {
          "objectType": {"type": "string"},
          "objectKey": {"type": "string"},
          "allowedFields": {"type": "object"},
          "effectiveFrom": {"type": ["string", "null"], "format": "date-time"},
          "effectiveTo": {"type": ["string", "null"], "format": "date-time"},
          "effectiveClass": {"enum": ["CURRENT_EFFECTIVE", "FUTURE_SCHEDULED", "UNDETERMINED"]},
          "recordDigest": {"type": "string", "pattern": "^[a-f0-9]{64}$"}
        }
      }
    },
    "coverage": {"$ref": "schema://coverage-record/1.0.0"},
    "nextPageToken": {"type": ["string", "null"]},
    "resultDigest": {"type": "string", "pattern": "^[a-f0-9]{64}$"}
  }
}
```

## 6. 源系统适配器 SPI

```java
public interface ConfigSourceAdapter {
  SourceDescriptor descriptor();
  QueryResult execute(ApprovedQuery query, EphemeralSecret secret);
  ScopePage enumerate(ApprovedEnumeration query, EphemeralSecret secret);
  RecheckResult recheck(ApprovedScope scope, PriorDigest prior, EphemeralSecret secret);
  ContractProbeResult probeContract(SourceContract contract);
}

public record ApprovedQuery(
    UUID taskId, UUID stepId, UUID approvalId,
    SourceBinding binding, QueryTemplate template,
    Map<String, TypedValue> parameters,
    Set<FieldId> requestedFields,
    FieldPolicy policy, IdempotencyKey idempotencyKey) {}
```

适配器执行管线：

1. 工具网关验证任务状态和步骤能力。
2. 根据 `SourceCatalog` 解析逻辑字段到真实字段；模型不可见真实 SQL。
3. 从环境注入的 secret provider 读取短期凭证。
4. 发送只读请求，限制超时、页大小和响应体。
5. 在可信内存边界验证返回字段；出现禁止读取字段时销毁响应并失败关闭。
6. 立即投影允许保留字段，规范化、分类、摘要。
7. 持久化最小快照、覆盖和调用状态；原响应不落盘。

### 6.1 API 与数据库适配器

| 项目 | API 适配器 | 数据库视图适配器 |
|---|---|---|
| 查询 | 固定 endpoint 和参数模板 | 预编译参数化 `SELECT` 模板 |
| 只读证明 | 服务账号权限和接口清单 | 只读账号、只读事务、视图授权、写负测 |
| 分页 | cursor/offset 语义登记 | keyset 优先；排序键必须稳定 |
| 版本 | ETag/version/watermark | 业务版本列或结果摘要 |
| 契约漂移 | 响应 schema 验证 | 元数据/列投影验证 |
| 超时结果 | 按接口幂等语义处理 | 查询取消并标记调用状态 |

## 7. SourceCatalog 最小模型

```yaml
catalogVersion: TBC-SRC-01
objects:
  - logicalObject: TBC-SRC-OBJECT-01
    authority: PRIMARY|SECONDARY|CROSS_CHECK
    channel: API|READ_ONLY_VIEW
    sourceBindingId: TBC-SRC-BINDING-01
    queryTemplates:
      - id: TBC-SRC-TEMPLATE-01
        version: TBC
        purpose: enumerate|lookup|recheck
        parameterSchemaRef: TBC
        stableOrder: TBC
        pagination: TBC
        watermark: TBC
    fields:
      - logicalField: TBC-SRC-FIELD-01
        physicalField: TBC
        type: TBC
        readable: TBC
        retainable: TBC
        modelAllowed: TBC
        effectiveTimeRole: none|from|to
    completenessClaim: TBC
    conflictPolicy: UNKNOWN_SOURCE_CONFLICT
```

## 8. ModelGateway 契约

```java
public interface ModelGateway {
  <T> ModelResult<T> generateStructured(
      ModelPurpose purpose,
      PromptTemplateVersion template,
      AllowedContext context,
      JsonSchema<T> outputSchema,
      ModelBudget budget,
      CancellationToken cancellationToken);
}
```

`AllowedContext` 只能引用经字段策略投影的证据、已发布知识片段和任务元数据。生产只有一个实现；失败不切换模型。调用记录包括模型绑定版本、模板版本、输入证据 ID、字段清单摘要、输入/输出哈希、token、延迟、完成原因和 schema 校验结果，不长期保存完整原文。

### 8.1 下一步提议 schema

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "object",
  "additionalProperties": false,
  "required": ["recommended", "alternatives", "reason", "stopAssessment"],
  "properties": {
    "recommended": {"$ref": "#/$defs/action"},
    "alternatives": {"type": "array", "maxItems": 2, "items": {"$ref": "#/$defs/action"}},
    "reason": {"type": "string", "maxLength": 2000},
    "stopAssessment": {"enum": ["CONTINUE", "ENOUGH_FOR_PARTIAL_PACKET", "ENOUGH_FOR_COMPLETE_PACKET"]}
  },
  "$defs": {
    "action": {
      "type": "object",
      "additionalProperties": false,
      "required": ["goal", "toolCode", "sourceBindingId", "scopeRevisionId", "requestedLogicalFields", "queryTemplateVersion", "maxCalls"],
      "properties": {
        "goal": {"type": "string", "maxLength": 2000},
        "toolCode": {"type": "string"},
        "sourceBindingId": {"type": "string", "format": "uuid"},
        "scopeRevisionId": {"type": "string", "format": "uuid"},
        "requestedLogicalFields": {"type": "array", "uniqueItems": true, "items": {"type": "string"}},
        "queryTemplateVersion": {"type": "string"},
        "maxCalls": {"type": "integer", "minimum": 1, "maximum": 100}
      }
    }
  }
}
```

模型输出通过 schema 后仍需业务验证器确认工具存在、字段获准、范围一致和预算允许，才生成审批卡。

## 9. 发布和查看契约

发布命令必须带准确草稿摘要和 review ID。服务先锁任务，验证 review、范围、运行版本和最新复核结果，再在一个事务写入修订、审计和 outbox。重放同一幂等键返回原修订；相同草稿不同命令不得生成重复修订。

`GET /published-packets/{revisionId}` 对 `OPERATOR` 和 `VIEWER` 返回同一 canonical view model。授权不同只影响能否访问，不改变内容。未发布草稿不对 `VIEWER` 可见。

## 10. 契约测试

| 测试 | 预期 |
|---|---|
| 未声明字段进入 API 请求 | 400，`additionalProperties=false` 生效 |
| 模型提议第 3 个备选 | schema 失败，不生成步骤 |
| 模型请求目录外工具/字段 | 业务验证失败，不产生能力令牌 |
| API/DB 返回禁止读取字段 | 整次调用失败，敏感值全链路不可搜索 |
| 返回允许读但禁止留存字段 | 可信内存裁剪，只有批准字段持久化/送模 |
| 分页 token 重复或总量变化 | 覆盖 PARTIAL/UNKNOWN，禁止全范围否定 |
| 相同幂等键重复发布 | 返回同一修订，无重复审计业务事件 |
| 草稿摘要与 review 不一致 | 409 `DRAFT_STALE` |
| 来源 schema 漂移 | `SOURCE_CONTRACT_DRIFT`，暂停该来源 |

## 11. P0 待确认契约

| 编号 | 待确认事实 | 证据 | 未确认时阻断 |
|---|---|---|---|
| `TBC-SRC-01` | SourceCatalog 对象和版本 | 负责人签署目录 | 真实源调用 |
| `TBC-SRC-02` | 允许读取/保留/送模字段 | 三列权限矩阵 | 真实数据持久化和送模 |
| `TBC-SRC-03` | API/视图权威级别及冲突规则 | 对象级责任确认 | 确认配置事实 |
| `TBC-SRC-04` | 分页、稳定排序、总量和水位 | 契约测试记录 | 全范围否定结论 |
| `TBC-SRC-05` | 生效时间/版本语义 | 样本和负责人说明 | 当前/未来确定性分类 |
| `TBC-SRC-06` | 只读凭证及写入负测 | 权限清单和测试证据 | 生产接入 |
| `TBC-SRC-07` | 超时、重试、限流和故障语义 | 接口 SLA/测试 | 自动有限重试 |
| `TBC-AUTH-01` | 统一认证协议、issuer/audience/claims | 身份契约 | 统一登录 |
| `TBC-MODEL-01` | 模型端点、上下文、限额和结构化能力 | 测试报告 | 模型步骤 |
| `TBC-OBJ-01` | 对象存储协议 | 平台能力确认 | 大原件和大导出留存 |

