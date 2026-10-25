# Spec Delta

## Purpose

强化共享 JSON 摘要与序列化契约：全项目 SHA-256 摘要仅经 `Sha256Digest` 一处实现，业务 JSON 序列化仅经统一 `JsonCodec` 一处入口，严格解析复用 `CanonicalJsonCodec`，避免业务模块复制基础算法。

## MODIFIED Requirements

### Requirement: Shared serialization contract is reused by audit digests

系统 SHALL 让审计摘要复用共享 canonical JSON 和 SHA-256 实现，避免业务模块复制基础算法。

#### Scenario: Audit digest follows shared canonicalization vector

- **WHEN** 审计 payload 使用共享测试向量计算摘要
- **THEN** 审计 payload digest 与共享摘要结果一致

#### Scenario: Business modules reuse shared SHA-256 entry point

- **WHEN** 任意业务模块需要计算 SHA-256 摘要或进行流式摘要
- **THEN** 通过 `Sha256Digest.hex(String)`、`Sha256Digest.hex(byte[])` 或 `Sha256Digest.newDigest()` 完成，不内联 `MessageDigest` 或手写十六进制转换

#### Scenario: Business JSON serialization uses unified mapper

- **WHEN** 业务模块需要解析或序列化 JSON（非审计 canonical 场景）
- **THEN** 通过 `JsonCodec` 统一入口完成，不在模块内新建 `JsonMapper` 实例

#### Scenario: Strict JSON parsing reuses canonical codec

- **WHEN** 需要严格解析 JSON 并拒绝尾随 token
- **THEN** 复用 `CanonicalJsonCodec.parse`，不在模块内重复配置严格 `JsonMapper`
