---
id: 20260926-strict-versioned-json-validation
title: "将 VersionedJson 的首尾字符校验改为 Jackson 严格解析，并补充基础能力复用规范"
status: done
priority: P2
owner: yigeyige99
created_at: 2026-09-26
type: custom
source: conversation
openspec_change: strict-versioned-json-validation
---

## 需求

### ID&标题

- 自定义需求：将 VersionedJson 的首尾字符校验改为 Jackson 严格解析，并补充基础能力复用规范

### 描述

- 使用项目现有 Jackson 能力严格解析 `VersionedJson` JSON 内容。
- 将“尽量不要手写基础能力或重复造轮子；引入新框架组件前需用户确认”的约束写入后端编码规范 Skill。

## 背景

当前 `VersionedJson` 仅通过首尾字符判断 JSON 容器，无法识别结构非法的 JSON。项目已具备 Spring Boot Web 提供的 Jackson 能力，不需要新增依赖。

## 目标

- [x] 使用 Jackson 严格校验 JSON object/array 内容。
- [x] 保持 `requireObject()` 的领域语义和现有异常类型兼容。
- [x] 补充后端 Skill 的基础能力复用和依赖引入确认规则。
- [x] 补充或调整测试，覆盖非法 JSON 和 object/array 边界。

## 验收标准

- 非法 JSON 无法构造 `VersionedJson`。
- 合法 object/array JSON 可正常构造；`requireObject()` 行为保持正确。
- 后端 Skill 明确要求优先复用现有框架/通用组件，新增组件须先获得用户确认。
- 相关 Maven 测试通过。

## 执行记录

- 2026-09-26：创建任务，准备按 OpenSpec 实施。
- 2026-09-26：使用 Jackson `JsonMapper` 严格解析 JSON，并启用尾随 token 校验；补充合法/非法 JSON 单元测试。
- 2026-09-26：更新后端编码规范，要求优先复用现有框架或通用组件，引入新组件前先取得用户确认。
- 2026-09-26：`mvn -f backend/pom.xml -Dtest=TaskDomainModelTest test` 通过，3 个测试通过。
- 2026-09-26：`mvn -f backend/pom.xml test` 通过，48 个测试通过。
