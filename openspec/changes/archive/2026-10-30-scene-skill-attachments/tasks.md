## 1. 附件持久化

- [x] 1.1 新增 V27 建表 `task.business_scene_skill_attachment`（含 skill 索引）
- [x] 1.2 新增领域 `SceneSkillAttachment` 与 `SceneSkillAttachmentRepository` 及 MyBatis 实现
- [x] 1.3 新增 `SceneSkillAttachmentService`：list/upload/delete，upload 复用 extractor 并校验 5 个/100KB/摘要必填

## 2. 附件接口

- [x] 2.1 场景 Skill 下新增附件上传、列表、删除接口与响应契约

## 3. 模型两段式按需

- [x] 3.1 `BusinessGuidanceSuggestion` 增加 `requestedAttachments` 字段与严格解析
- [x] 3.2 `BusinessGuidanceContext` 承载附件清单与正文；模型客户端输入注入附件目录与正文
- [x] 3.3 `guideBusinessHandling` 内部两段式：清单注入→模型声明→按需注入正文（≤2 个完整正文）

## 4. 前端

- [x] 4.1 场景 Skill 编辑弹窗新增附件上传（选文件+填摘要）、列表与删除
- [x] 4.2 前端附件 CRUD API 与类型契约

## 5. 验证

- [x] 5.1 相关后端测试、编译、前端类型检查与构建、OpenSpec 严格校验通过
