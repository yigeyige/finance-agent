## Why

草稿生成后，批准范围内的对象可能新增、删除或发生字段、生效分类、来源、规则和模板版本变化。发布前若只比较来源版本或只重查旧对象，会把过期草稿错误地送入发布流程。

## What Changes

- 增加基于批准范围重新枚举的范围重查服务和差异模型。
- 比较新增、删除、字段摘要、生效分类、来源版本、规则版本和模板版本。
- 输出可追溯重查记录，并将有影响变化的草稿标记为 `STALE_DRAFT`；无影响变化才允许 `READY_TO_PUBLISH`。
- 增加结果仓储与新增引用、字段变化、版本变化等测试向量。

## Capabilities

### New Capabilities

- `pre-publish-recheck`: 发布前全范围重查、差异识别和草稿影响判定。

### Modified Capabilities

<!-- No existing requirement changes. -->

## Impact

- 影响 packet/evidence 应用层和新的范围重查领域模型及测试。
- 不新增依赖、不修改数据库表；仓储先提供进程内可替换实现。
