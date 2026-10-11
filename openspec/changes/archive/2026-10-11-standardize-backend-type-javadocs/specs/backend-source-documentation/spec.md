## MODIFIED Requirements

### Requirement: 生产后端类型具有中文职责说明

系统 SHALL 为 `backend/src/main/java` 中的每个非包描述 Java 生产顶层类型提供中文 Javadoc，说明其业务职责、边界或适用场景。每个类型 Javadoc MUST 位于关联类型注解之前，并且使用以下字段：`@Description`、当前构建项目版本的 `@Version`、`@Author: yigeyige99` 和格式为 `yyyy-MM-dd HH:mm:ss` 的 `@Date`。

#### Scenario: 维护人员查看任一生产类型

- **WHEN** 维护人员在 IDE 中查看应用、配置、诊断或对象存储模块的任一生产顶层类型
- **THEN** 可以在类型连续注解之前直接读取与当前实现一致、包含完整元数据模板的中文职责说明
