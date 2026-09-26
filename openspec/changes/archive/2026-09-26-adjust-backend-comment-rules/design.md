## Context

上一次注释补充将部分 Javadoc 写在注解之后，并覆盖了简单访问器和构造器。

## Goals / Non-Goals

**Goals:** 规范注释位置，减少无业务价值的方法注释。

**Non-Goals:** 不改变 Java 代码行为或公开契约。

## Decisions

- Javadoc 位于关联的连续注解之前。
- 构造器、简单 getter/setter 与职责显而易见的简单方法不添加 Javadoc；其余方法按模板保留。

## Risks / Trade-offs

- [简单方法判断不一致] → 以无校验、无分支、无副作用的访问器或委托为界。
