## Context

当前应用只有 `finance-agent.storage.mode` 生产配置守卫，没有对象存储端口。P0 的权威任务、证据、决策包 JSON/HTML 仍由后续 PostgreSQL 领域任务承载；本变更只为大原件/导出物预留可替换边界。local/test 需要可测试的临时实现，prod 必须在正式适配器未接入时明确失败。

## Goals / Non-Goals

**Goals:**

- 提供不暴露宿主机绝对路径的对象引用和 `ObjectStorageGateway` 端口。
- 为 local/test 提供流式 SHA-256、大小上限、相对逻辑键和根目录隔离的临时实现。
- 为 prod 提供始终失败的 disabled 实现，并在启动校验中拒绝缺失正式适配器标识。
- 保持对象存储可选；P0 核心决策包 JSON/HTML 的权威保存边界仍是 PostgreSQL。

**Non-Goals:**

- 不接入 S3、Azure Blob、MinIO 或任何正式对象存储 SDK。
- 不把对象引用作为任务/证据/决策包数据库模型，不实现保留期、清理编排或上传 API。
- 不把本地临时目录当作生产降级路径。

## Decisions

### Contract and object reference

- `storage.application.ObjectStorageGateway` 只接收逻辑键、输入流和媒体类型，返回 `storage.domain.ObjectReference`。
- `ObjectReference` 只包含相对 `objectKey`、64 位十六进制 SHA-256、字节数和媒体类型；不包含 `Path`、URI、宿主机根目录或凭证。
- 失败使用明确的 `ObjectStorageNotConfiguredException`、`ObjectStoragePathViolationException` 和 `ObjectStorageLimitExceededException`，避免用 null 表示未配置或失败。

### Temporary implementation

- local/test profile 注册 `LocalTemporaryObjectStorageGateway`，根目录和最大字节数来自 `finance-agent.storage.temporary-root` 与 `max-bytes`。
- 写入先落 `.upload-*.tmp`，流式计算摘要和大小，超过上限立即删除临时文件；成功后移动到经过校验的逻辑键路径。
- 逻辑键必须是非空相对键，只允许安全字符和 `/` 分隔，不允许驱动器前缀、反斜杠、`.`、`..` 或归一化后越出根目录。
- 读取和删除同样重新校验逻辑键；实现不向领域层返回绝对路径。

### Production guard

- prod profile 注册 `DisabledObjectStorageGateway`，所有操作抛出未配置异常，不读取 `temporary-root`，不尝试本地回退。
- `ProductionStartupValidator` 要求 `storage.mode=OBJECT_STORE` 且 `storage.adapter-id` 非空且不是 `none`；适配器具体实现留给后续任务。
- local/test 的 `adapter-id=temporary` 仅是环境标识，不允许被 prod 配置复用。

### Configuration and documentation

- 新增 `storage.adapter-id`、`storage.temporary-root`、`storage.max-bytes`，并为 local/test 提供仓库内 `.runtime` 默认目录；prod 默认 adapter-id 为 `none`、temporary-root 为空。
- 文档明确 PostgreSQL 保存决策包 JSON/HTML，临时对象仅用于后续大原件/导出物验证；本变更不创建数据库表或迁移。

## Risks / Trade-offs

- [Risk] 临时目录可能占用本地磁盘 → [Mitigation] 每次写入强制最大字节数，使用临时文件并由后续任务定义清理策略。
- [Risk] 逻辑键冲突覆盖已有对象 → [Mitigation] 引用包含内容摘要，调用方必须生成幂等逻辑键；正式对象存储接入前不提供业务上传 API。
- [Risk] 符号链接可绕过简单路径检查 → [Mitigation] 禁止用户提供绝对路径和 `..` 段，写入根目录由服务端配置；正式适配器接入前继续保持临时能力非生产可用。

## Migration Plan

1. 发布端口、配置守卫和 local/test 临时实现；不改变现有 PostgreSQL 数据模型。
2. 后续正式适配器实现 `ObjectStorageGateway` 并以明确 `adapter-id` 注册，先在 test 验证后再允许 prod。
3. 回滚时移除对象存储调用方或恢复到仅 PostgreSQL 的 P0 路径；prod 缺少适配器继续失败关闭。

## Open Questions

- 正式对象存储供应商、区域、保留期限、容量配额和凭证注入方式留待 P1/P2 平台确认。
