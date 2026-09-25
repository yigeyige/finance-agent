## ADDED Requirements

### Requirement: executable Spring Boot skeleton
系统 MUST 提供包名为 `cn.com.crc.ems.finance.agent`、应用名为 `finance-agent` 的 Spring Boot 应用入口，并能在不连接真实外部系统的情况下构建可执行 JAR。

#### Scenario: package executable jar
- **WHEN** 使用 Java 17 执行 `mvnw -f backend/pom.xml -q package -DskipTests`
- **THEN** 构建成功并生成包含应用入口的可执行 JAR

### Requirement: modular package boundaries
系统 MUST 为 `iam`、`task`、`evidence`、`packet`、`harness`、`source`、`knowledge`、`governance`、`shared` 建立 application、domain、infrastructure 分层包，并为占位包提供 package-info 文档。

#### Scenario: module packages are discoverable
- **WHEN** 检查后端源码包结构
- **THEN** 九个模块和三层职责均可通过包路径识别，且不需要依赖具体外部系统

### Requirement: architecture dependency guard
系统 MUST 使用 ArchUnit 测试阻止领域层依赖 Web、数据库具体实现或基础设施层，并阻止发布/业务领域代码直接依赖 Harness 内部状态实现。

#### Scenario: forbidden reverse dependency fails architecture test
- **WHEN** 任一 `..domain..` 类依赖 Spring Web、Spring JDBC、JPA、任一 `..infrastructure..` 类或 Harness 内部状态包
- **THEN** `*ArchitectureTest` 必须失败并指出违规依赖
