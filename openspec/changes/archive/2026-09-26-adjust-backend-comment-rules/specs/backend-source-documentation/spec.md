## MODIFIED Requirements

### Requirement: 生产后端成员具有中文语义说明

系统 SHALL 将保留的生产源码 Javadoc 置于关联注解之前；构造器、简单 getter/setter 和职责显而易见的简单方法无需注释，其他具有业务语义、校验、存储、诊断或异常处理的成员必须使用中文 Javadoc 说明其主要语义或约束。

#### Scenario: 维护人员查看带注解的成员

- **WHEN** 维护人员查看具有 Javadoc 和注解的生产成员
- **THEN** Javadoc 位于该成员连续注解之前

#### Scenario: 维护人员查看简单成员

- **WHEN** 维护人员查看构造器、简单访问器或职责显而易见的简单方法
- **THEN** 源码不要求提供冗余 Javadoc
