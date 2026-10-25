# Design

## Context

- 现状：`shared/domain` 已有 `Sha256Digest`（仅 `hex(String)`）、`CanonicalJsonCodec`（严格解析 `FAIL_ON_TRAILING_TOKENS` + `exactBigDecimals` + canonical 编码，供审计摘要）。但业务模块散落重复实现。
- `JsonMapper`（Jackson 3.x 的 `com.fasterxml.jackson.databind.json.JsonMapper`）是线程安全的，可安全共享单一实例。
- Spring Boot 自动配置一个 `ObjectMapper` Bean（含时间模块等统一配置），Spring 组件应优先注入而非 `new`。
- 规范 `.codex/skills/backend-coding-standards/SKILL.md`：JSON 使用统一序列化配置；编码/摘要复用成熟能力；复用优先。

## Goals / Non-Goals

**Goals:**
- 全项目 SHA-256 摘要仅经 `Sha256Digest` 一处实现。
- 全项目业务 JSON 序列化仅经 `JsonCodec` 一处通用入口。
- 严格解析复用 `CanonicalJsonCodec`。

**Non-Goals:**
- 不改动 `CanonicalJsonCodec` 的 canonical 编码算法与 `exactBigDecimals` 语义（审计摘要专用）。
- 不引入 Hutool / Commons 等新依赖。
- 不改数据库、不改前端、不改变对外接口契约。

## Decisions

### 1. 扩展 `Sha256Digest`（不新建摘要工具）

```java
public static String hex(String value)   // 已有
public static String hex(byte[] bytes)   // 新增：HexFormat.of().formatHex(digest)
public static MessageDigest newDigest()  // 新增：流式摘要获取 MessageDigest 实例
```

- `hex(byte[])` 内部用 Java 17 `HexFormat.of().formatHex()`，替代散落的手写 `String.format("%02x")`。
- `newDigest()` 供文件流式摘要场景（`ControlledWorkspaceManager`、`LocalTemporaryObjectStorageGateway`）复用，避免再内联 `MessageDigest.getInstance("SHA-256")`。

### 2. 新增 `shared/domain/JsonCodec`（通用 JSON 入口）

持有一个 `private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();`（默认配置，线程安全），提供静态方法：

- `JsonNode readTree(String json)`：默认宽松解析（业务场景）
- `<T> T readValue(String json, Class<T> type)`
- `String writeValueAsString(Object value)`
- 统一把 `JsonProcessingException` 包装为 `IllegalArgumentException`/`IllegalStateException`

边界：`JsonCodec` 只服务**普通业务序列化/反序列化**；`CanonicalJsonCodec` 继续负责**严格解析 + canonical 编码**（审计摘要），两者职责不混。

### 3. 替换散落 JsonMapper（10 处默认实例）

将以下类的 `private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();` 删除，改用 `JsonCodec.*`：

`StateReference`、`BusinessDecisionPacketService`、`BusinessEvidenceAccumResponse`、`BusinessDecisionPacketResponse`、`TaskIntakeApplicationService`、`TaskAuditEventAppender`、`TestBusinessSourceAdapter`（变量名 `JSON`）、`BusinessModelInteractionService`、`PacketPublicationService`、`PacketDraftAssembler`。

### 4. `VersionedJson` 严格解析复用 `CanonicalJsonCodec`

删除 `VersionedJson.JSON_MAPPER`（`FAIL_ON_TRAILING_TOKENS` 严格实例），`parse` 改为 `CanonicalJsonCodec.parse(value)`。两者语义一致（严格解析拒绝尾随 token）；`exactBigDecimals` 只影响数字节点类型，不影响 `isObject()/isArray()` 判断，安全。

### 5. `JacksonAgentStateCodec` 保持不变

`JacksonAgentStateCodec` 保留其默认构造器 `new ObjectMapper()`。当前 Spring Boot 4 未自动装配 `com.fasterxml.jackson.databind.ObjectMapper` Bean（容器只提供 `JsonMapper`，不提供旧 `ObjectMapper` 类型），因此不能改为仅构造器注入；AgentScope State 编解码继续自建 `ObjectMapper` 并保持原有可注入构造器供测试使用。

## Risks / Trade-offs

- `JsonCodec` 使用默认 JsonMapper（非严格、非 exactBigDecimals），与原散落的 `JsonMapper.builder().build()` 行为完全一致，无回归风险。
- `VersionedJson` 改用 `CanonicalJsonCodec.parse` 后数字节点类型可能从 `Double` 变为 `BigDecimal`——但 `VersionedJson` 只用解析结果判断容器类型，不读取数字，行为不变；已有单测覆盖严格解析与容器校验。

## Migration Plan

- 纯代码重构，无数据库迁移、无配置变更。
- 验证：`mvn -q compile` + `mvn -q test`（重点跑涉及 `Sha256Digest`、`CanonicalJsonCodec`、`VersionedJson`、`JacksonAgentStateCodec`、`StateReference` 的单测）。
