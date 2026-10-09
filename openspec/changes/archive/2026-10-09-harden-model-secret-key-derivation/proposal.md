## Why

当前 AES-GCM 实现将部署主密钥按字符截取前 32 位直接转为 UTF-8 字节，既可能在多字节字符场景产生非法 AES 长度，也会忽略主密钥后续内容。直接替换会破坏现有内网数据库中已经加密的模型 API Key。

## What Changes

- 新写入密文使用完整主密钥 SHA-256 派生的 AES-256 Key，并添加格式版本前缀。
- 读取时保留旧密文格式的兼容解密路径，新格式始终使用派生 Key。
- 添加无敏感值的单元测试，验证多字节主密钥、旧格式兼容及认证失败。

## Capabilities

### Modified Capabilities

- `model-connection-management`: 明确模型 API Key 的加密密钥派生、密文版本与旧格式兼容边界。

## Impact

- 模型 API Key 加密基础设施与单元测试。
- 模型连接安全规格与配置说明。
- 不改变 API、数据库列、模型配置页面或部署变量名称。
