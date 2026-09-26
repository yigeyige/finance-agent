## Why

后端已有启动、配置、诊断和对象存储等基础能力，但源代码对业务职责、配置约束和安全边界的中文说明不完整，维护人员需要额外追踪实现才能理解意图。现在补齐与实现同步的简要中文注释，可降低后续迭代和排障的理解成本。

## What Changes

- 为 `backend/src/main/java` 中的应用、配置、诊断和对象存储生产代码补充类型级中文 Javadoc。
- 为生产代码中的构造器、公开或非公开方法、接口方法及嵌套类型补充与职责一致的简要中文 Javadoc。
- 统一说明配置读取或写入、输入约束、异常语义和安全处理意图；不变更可执行逻辑或 API 契约。

## Capabilities

### New Capabilities

- `backend-source-documentation`: 生产后端源码具备一致、简要且可维护的中文类型与方法说明。

### Modified Capabilities

无。

## Impact

- 受影响代码：`backend/src/main/java` 的 Java 生产源码及其既有 `package-info.java` 包说明。
- API、配置键、依赖版本、数据库结构和运行时行为均不变。
- 验证方式：后端 Maven 测试与源码范围检查。
