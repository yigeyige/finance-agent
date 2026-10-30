## Context

现有 `BusinessGuidanceAttachmentExtractor` 已能把 json/xml/txt/md/csv（UTF-8 原文）与 xls/xlsx（FastExcel 流式转文本）统一成文本投影，且现有 `guideBusinessHandling` 已有 EMS 数据库多轮按需查询的先例。本切片复用这两者，把场景附件做成持久化、可复用、按需注入的调查上下文。

## Goals / Non-Goals

**Goals:** 场景附件持久化 + CRUD；调查阶段两段式按需注入正文。
**Non-Goals:** 不做决策包阶段附件、不做对象存储、不做附件版本管理、不改变现有任务级临时附件。

## Decisions

- 表 `task.business_scene_skill_attachment`：`attachment_id / skill_id / file_name / content_type / summary / content_text / created_by / created_at / updated_by / updated_at`；`content_text` 存完整文本投影不截断，`summary` 由管理员上传时手填（≤500 字符）。
- 约束：每场景最多 5 个附件，单文件 ≤100KB，仅 json/xml/xlsx/txt/md/csv。
- 领域 record `SceneSkillAttachment` + `SceneSkillAttachmentRepository`（findBySkillId、countBySkillId、findBySkillIdAndFileNames、insert、deleteById）+ `SceneSkillAttachmentService`（list/upload/delete）。
- 接口挂在场景 Skill 下：`POST/GET /api/v1/business-scene-skills/{skillId}/attachments`、`DELETE .../attachments/{attachmentId}`；上传为 multipart（files + summary）。
- 模型协议：`BusinessGuidanceSuggestion` 增加 `requestedAttachments: List<String>`（第一轮模型返回需要的文件名，≤2）；`GUIDE_HANDLING` 模板同步要求模型按附件目录声明所需正文。
- 两段式流程在 `BusinessModelInteractionService.guideBusinessHandling` 内部：先按任务关联场景读取附件清单（fileName+summary）注入第一轮；模型返回 `requestedAttachments` 后，按文件名查正文，把至多 2 个附件完整正文注入第二轮；无请求或清单为空则一轮结束。

## Risks / Trade-offs

两段式会在模型请求附件正文时多一次模型调用；用附件数量与注入上限（≤2）控制耗时。附件正文完整注入不截断，依赖模型 1M 上下文上限。

## Migration Plan

新增 `V27__business_scene_skill_attachment.sql` 建表；纯新增，不影响既有数据。

## Open Questions

无。
