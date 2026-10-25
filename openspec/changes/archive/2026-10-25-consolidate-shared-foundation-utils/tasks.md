# Tasks

## 1. 扩展 Sha256Digest

- [ ] 1.1 在 `shared/domain/Sha256Digest` 增加 `hex(byte[])` 与 `newDigest()`，`hex(byte[])` 用 `HexFormat.of().formatHex()`，验证编译通过

## 2. 收敛 SHA-256 摘要实现

- [ ] 2.1 `storage/infrastructure/LocalTemporaryObjectStorageGateway`：删除私有 `sha256()`/`hex()`，改用 `Sha256Digest.newDigest()` 与 `Sha256Digest.hex(byte[])`，验证编译通过
- [ ] 2.2 `harness/infrastructure/ControlledWorkspaceManager`：同上，删除私有 `sha256()`/`hex()` 与 `String.format(Locale.ROOT, "%02x", ...)`，验证编译通过
- [ ] 2.3 `harness/application/AllowedContext`、`harness/application/DeterministicModelStub`：内联 `MessageDigest` + `HexFormat` 改为 `Sha256Digest.hex(...)`，验证编译通过（`AesGcmModelApiKeyCipher.derive` 为密钥派生，digest bytes 作 AES 密钥，不纳入摘要收敛）

## 3. 新增 JsonCodec 并统一 JSON 序列化

- [ ] 3.1 在 `shared/domain` 新增 `JsonCodec`（共享默认 `JsonMapper` + `readTree`/`readValue`/`writeValueAsString`），验证编译通过
- [ ] 3.2 替换 10 处散落 `JsonMapper.builder().build()`（StateReference、BusinessDecisionPacketService、BusinessEvidenceAccumResponse、BusinessDecisionPacketResponse、TaskIntakeApplicationService、TaskAuditEventAppender、TestBusinessSourceAdapter、BusinessModelInteractionService、PacketPublicationService、PacketDraftAssembler）为 `JsonCodec.*`，验证编译通过
- [ ] 3.3 `task/domain/VersionedJson` 删除私有 JsonMapper，`parse` 复用 `CanonicalJsonCodec.parse`，验证编译通过（`JacksonAgentStateCodec` 保留默认 `new ObjectMapper()`，Spring Boot 4 无自动 `ObjectMapper` Bean）

## 4. 验证

- [ ] 4.1 后端 `mvn -q compile` 通过
- [ ] 4.2 后端 `mvn -q test` 全量通过（重点覆盖 Sha256Digest / CanonicalJsonCodec / VersionedJson / JacksonAgentStateCodec / StateReference）

## Workflow follow-up

- 更新 `tasks/yigeyige99/01.doing/task-index.md` 中本任务完成状态
- 归档 change：openspec-sync-specs 同步 delta spec，再 openspec-archive-change 归档
- 创建 commit（message 格式 `<type>: <task-name>`）
