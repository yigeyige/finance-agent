## Why

业务场景 Skill 需要沉淀可复用的知识附件（json/xml/xlsx 等配置或说明文件），供模型在调查分析时参考；但不应把全部附件正文一股脑送入模型浪费 token，而应按模型需要按需调用。

## What Changes

- 新增业务场景附件持久化表 `business_scene_skill_attachment`，保存上传文件的文本投影（json/xml 原文、xlsx 流式转文本）。
- 新增附件上传、列表、删除接口，复用现有 `BusinessGuidanceAttachmentExtractor` 完成格式校验与文本投影。
- 前端业务场景 Skill 编辑弹窗新增附件上传与摘要填写。
- 调查阶段 `guideBusinessHandling` 内部改为两段式：第一轮只给模型附件清单（文件名+摘要），模型声明需要的附件，第二轮按需注入至多 2 个附件的完整正文。

## Capabilities

### New Capabilities
- `business-scene-skill-attachment`: 场景 Skill 附件的持久化与模型按需使用。

### Modified Capabilities
无。

## Impact

只扩展调查阶段模型上下文与场景 Skill 维护界面；决策包阶段暂不使用附件。现有任务级临时附件保持不变。
