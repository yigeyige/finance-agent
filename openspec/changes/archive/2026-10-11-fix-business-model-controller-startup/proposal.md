## Why

本地运行包含附件分析改动的 JAR 时，`BusinessModelController` 的两个构造器未明确 Spring 注入入口，导致应用上下文创建控制器 Bean 失败，阻断健康检查与附件功能的运行时验证。

## What Changes

- 明确业务模型控制器的 Spring 构造器注入入口，同时保留包内测试替身构造器。
- 增加最小启动上下文回归测试，防止控制器装配失败再次进入可发布制品。
- 以独立端口启动本地实例并验证健康与就绪端点。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-model-interaction`：要求模型交互控制器在本地应用上下文中可被装配，且保持既有接口映射。

## Impact

- 修改 `BusinessModelController` 的注入标记与其测试。
- 不修改 API 路径、请求/响应契约、数据库或依赖。
