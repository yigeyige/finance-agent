## ADDED Requirements

### Requirement: Model interaction controller is bootstrappable
系统 MUST 在本地应用上下文启动时完成业务模型交互控制器的 Bean 装配，并保持既有初稿、方案和证据分析接口映射可用。

#### Scenario: Local application context starts
- **WHEN** 应用以 `local` profile 启动且模型交互服务依赖可用
- **THEN** 系统成功创建业务模型交互控制器，健康与就绪端点可用，且不因控制器构造器歧义而终止启动
