## Context

后端当前仅包含 `AgentScopeApiProbe`，POM 已锁定 Java 17、Spring Boot 4.0.4 和 AgentScope 2.0.3，但没有 Spring Boot 应用入口、业务模块边界或可执行 JAR 配置。后续任务需要在单体仓库中按领域模块演进，同时避免领域代码直接绑定 Web、数据库和 Harness 具体实现。

## Goals / Non-Goals

**Goals:**

- 建立 `cn.com.crc.ems.finance.agent` 根包和 `FinanceAgentApplication`。
- 为九个业务模块建立 application/domain/infrastructure 占位包与 `package-info.java`。
- 用 ArchUnit 固化领域层和模块基础设施边界。
- 启用 Spring Boot repackage，使 `mvn package` 生成可执行 JAR。

**Non-Goals:**

- 不实现业务用例、数据库仓储、HTTP API 或 Harness 运行逻辑。
- 不启动应用、不连接外部系统、不改变前端和部署文件。

## Decisions

- **包结构而非 Maven 多模块**：当前目标是模块化单体骨架，使用单 POM + Java package 边界，避免过早拆分 Maven module；后续若构建隔离有明确收益再拆分。
- **分层占位**：每个业务模块包含 `application`、`domain`、`infrastructure` 三层 package-info，领域层仅放业务对象和规则，基础设施层承载外部适配。
- **应用入口**：`@SpringBootApplication` 放在根包，保证后续各模块组件可被统一扫描；应用名通过 `spring.application.name=finance-agent` 固定。
- **架构门禁**：使用 ArchUnit JUnit 5，规则禁止 `..domain..` 依赖 Spring Web/JDBC、JPA 或 `..infrastructure..`，并禁止领域包依赖其他模块基础设施包。规则通过测试表达反向依赖一旦出现就失败。
- **依赖版本**：ArchUnit 使用显式稳定版本 1.4.1，仅用于测试；业务依赖继续由 DEV-BOOT-002 基线管理。

## Risks / Trade-offs

- [Risk] 当前占位包没有业务类，部分架构规则暂时只保护未来类 → Mitigation：测试扫描根包并保留规则，后续每次新增类都会自动受门禁约束。
- [Risk] Spring Boot repackage 会要求应用入口 → Mitigation：本任务同时增加根包 `FinanceAgentApplication`，并用可执行 JAR 构建验证。
- [Risk] 单 POM 的 package 边界不是编译器强隔离 → Mitigation：ArchUnit 作为持续测试门禁，后续需要更强隔离时再评估 Maven 多模块。

## Migration Plan

无运行时迁移。执行 `mvnw -f backend/pom.xml -q test` 和 `mvnw -f backend/pom.xml -q package -DskipTests` 验证；用户后续自行启动 JAR。

## Open Questions

无。具体领域类和适配器由后续任务定义。
