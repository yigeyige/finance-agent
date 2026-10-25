# Proposal

## Why

后端只读审查发现两处"重复造轮子"，违反编码规范"复用优先 / 避免重复造轮子"：

1. SHA-256 摘要散落多处重复实现——项目已有 `shared/domain/Sha256Digest`，但 `LocalTemporaryObjectStorageGateway`、`ControlledWorkspaceManager` 各自重写 `sha256()` 与手写 `String.format("%02x")` 转十六进制；`AllowedContext`、`DeterministicModelStub` 直接内联 `MessageDigest` + `HexFormat` 计算摘要。
2. JSON 序列化散落 13 处各自新建 `JsonMapper.builder().build()`（部分甚至用 `new ObjectMapper()`），绕过统一序列化配置。

重复实现导致摘要算法与序列化配置不一致，维护成本上升，且已存在的基础能力契约（`canonical-time-identity-foundation`）被架空。

## What Changes

- 扩展 `shared/domain/Sha256Digest`：新增 `hex(byte[])` 与 `newDigest()`，作为全项目唯一 SHA-256 摘要入口。
- 新增 `shared/domain/JsonCodec`：统一业务 JSON 解析/序列化入口（单一共享线程安全的 `JsonMapper` 实例），替换 10 处散落的默认 `JsonMapper.builder().build()`。
- `task/domain/VersionedJson` 的严格解析复用 `CanonicalJsonCodec.parse`，删除其私有 JsonMapper。

## Capabilities

### Modified Capabilities

- `canonical-time-identity-foundation`: 强化共享 JSON 摘要与序列化契约——全项目仅保留单一 SHA-256 摘要实现与单一通用 JsonMapper；严格解析复用 `CanonicalJsonCodec`；canonical 审计编码仍由 `CanonicalJsonCodec` 专责。

## Impact

- 后端 `shared/domain`（`Sha256Digest` 扩展、新增 `JsonCodec`）
- 摘要收敛：`storage/infrastructure/LocalTemporaryObjectStorageGateway`、`harness/infrastructure/ControlledWorkspaceManager`、`harness/application/AllowedContext`、`harness/application/DeterministicModelStub`（`AesGcmModelApiKeyCipher.derive` 为密钥派生、digest bytes 作 AES 密钥，非摘要展示，不纳入收敛）
- JSON 收敛：`task/domain/VersionedJson`，以及 `StateReference`、`BusinessDecisionPacketService`、`BusinessEvidenceAccumResponse`、`BusinessDecisionPacketResponse`、`TaskIntakeApplicationService`、`TaskAuditEventAppender`、`TestBusinessSourceAdapter`、`BusinessModelInteractionService`、`PacketPublicationService`、`PacketDraftAssembler` 共 10 处散落 JsonMapper。
- 不引入新依赖，不改数据库，不改前端。
