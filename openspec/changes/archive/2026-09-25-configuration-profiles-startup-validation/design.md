## Context

工程已具备 Spring Boot 4.0.4 应用入口和模块化包骨架，但 `application.properties` 仅声明应用名称。后续任务需要同时支持本地 Stub、测试隔离和生产真实依赖，因此配置必须可绑定、可审计并在生产启动时阻断危险组合。用户负责本机环境变量注入和运行时手工验证，本变更只负责代码、配置、文档和静态/编译测试。

## Goals / Non-Goals

**Goals:**

- 以 `@ConfigurationProperties` 提供稳定、可测试的类型化配置对象。
- 通过 profile 文件为 local/test/prod 提供清晰默认值和环境变量覆盖点。
- 在 prod profile 启动阶段检查必需项和不允许的本地/测试能力。
- 确保异常文本和配置类日志表示不包含秘密值。

**Non-Goals:**

- 不在本任务中创建数据库连接、DeepSeek 客户端、对象存储实现或认证实现。
- 不读取 `.env` 文件，不提交任何真实凭据，不执行服务启动或部署。
- 不改变已有模块边界或引入新的依赖。

## Decisions

1. **使用 Spring Boot `@ConfigurationProperties` 绑定。**
   - 选择原因：支持 profile 合并、环境变量覆盖和独立单元测试；比散落的 `@Value` 更容易维护。
   - 替代方案：逐字段 `@Value`，但会分散默认值和校验逻辑，放弃。

2. **配置校验器仅在 `prod` profile 执行。**
   - 选择原因：local/test 必须允许 Stub 与本地存储以支持开发和自动化测试；生产安全约束集中在启动生命周期。
   - 替代方案：所有 profile 强制真实依赖，会阻塞本地开发，放弃。

3. **校验器暴露无副作用的静态校验入口。**
   - 选择原因：Spring Bean 使用 `@PostConstruct` 触发，单元测试可以直接验证失败契约而无需启动完整应用。
   - 替代方案：仅通过完整 `SpringApplication` 失败测试，执行慢且难以精确断言，放弃。

4. **敏感配置不实现可输出的 `toString`。**
   - 选择原因：避免调试日志或异常拼接出 API Key、密码和连接串；错误只返回属性名。

## Risks / Trade-offs

- [Risk] 生产环境变量命名错误会导致启动失败 → 错误列出缺失/非法配置项名称，并在文档提供完整清单。
- [Risk] profile 默认值被误用于生产 → prod 对关键值使用空默认值，并拒绝 Stub、测试身份和本地存储。
- [Risk] 新增配置字段与后续实现不一致 → 以配置说明、绑定测试和 OpenSpec 规格共同固定命名。

## Migration Plan

1. 合并配置类、profile 文件、`.env.example` 和启动校验器。
2. CI/本地执行配置绑定和生产失败契约测试，再执行 Maven 打包。
3. 部署时由运维注入 `.env.example` 中列出的环境变量；若失败，修正环境变量后重新启动即可回滚，无数据迁移。

## Open Questions

无。具体数据库、模型和对象存储实现由后续任务接入。
