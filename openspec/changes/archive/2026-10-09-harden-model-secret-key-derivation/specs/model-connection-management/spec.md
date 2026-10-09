## ADDED Requirements

### Requirement: Versioned model API key encryption format
系统 SHALL 对完整部署模型主密钥执行 SHA-256 派生 32 字节 AES-256 Key，并使用 AES-GCM 写入带 `v2:` 版本前缀的模型 API Key 密文；实现 MUST 使用随机 nonce 且不得记录主密钥、明文或密文。

#### Scenario: New API Key is saved with a multibyte master key
- **WHEN** 管理员在合规的多字节部署主密钥下保存模型 API Key
- **THEN** 系统成功写入带 `v2:` 前缀的认证密文，且可用同一主密钥解密

#### Scenario: Existing legacy ciphertext is read
- **WHEN** 数据库包含由旧截断规则写入的无前缀模型 API Key 密文
- **THEN** 系统使用兼容解密路径读取明文，但后续新写入不再产生旧格式

#### Scenario: Ciphertext authentication fails
- **WHEN** 模型 API Key 密文被篡改、格式损坏或主密钥不匹配
- **THEN** 系统以不含主密钥、明文或密文的安全错误失败
