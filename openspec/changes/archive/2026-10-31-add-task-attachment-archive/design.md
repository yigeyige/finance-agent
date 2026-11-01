## Context

`BusinessModelController.guidanceWithAttachments` 通过 `BusinessGuidanceAttachmentExtractor` 提取临时附件文本，直接传给模型，不落库。附件最大 3 个、单个 1MB、文本投影合计 24,000 字符。

## Decisions

### 1. 表结构

`task.task_attachment`：

- `attachment_id uuid` 主键
- `task_id uuid` 外键
- `file_name varchar(255)`
- `content_type varchar(200)`
- `size_bytes bigint`
- `sha256 char(64)`
- `content bytea` 原始文件
- `extracted_text text` 模型实际使用的文本投影
- `truncated boolean`
- `created_at timestamptz`
- 唯一约束 `(task_id, sha256)`

### 2. 落库时机

在 `attachmentExtractor.extract(files)` 校验成功后、调用 `interactionService.guideBusinessHandling` 之前落库。落库失败直接抛错，不继续模型调用，保证“无留痕不分析”。

### 3. 去重

按 `task_id + sha256` 去重；已存在则复用已有记录，不重复插入。

### 4. 文本投影一致性

`extracted_text` 保存 extractor 返回的 `BusinessGuidanceAttachment.content()`，即模型实际收到的文本（可能已截断）；`truncated` 同步保存。

### 5. 接口

- `GET /api/v1/tasks/{taskId}/attachments` 返回元数据，不返回正文。
- `GET /api/v1/tasks/{taskId}/attachments/{attachmentId}/download` 下载原始文件。

## Risks

- 附件含敏感业务数据 → 仅授权用户可访问；日志不输出附件正文。
- 1MB × 3 的 bytea 存储 → 可接受；后续可迁移对象存储。
- 上传重复文件 → SHA-256 去重。
