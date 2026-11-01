## 1. Data and domain

- [ ] 1.1 Flyway V34 新增 task.task_attachment
- [ ] 1.2 新增 TaskAttachment 领域对象、Entity、Mapper、Repository

## 2. Persistence flow

- [ ] 2.1 新增 TaskAttachmentService，保存原始字节、文本投影和 SHA-256
- [ ] 2.2 BusinessModelController 在模型调用前落库，失败中止
- [ ] 2.3 同一任务按 SHA-256 去重

## 3. API

- [ ] 3.1 任务附件列表接口
- [ ] 3.2 附件下载接口并校验任务归属

## 4. Verification

- [ ] 4.1 后端编译与相关测试通过
- [ ] 4.2 验证落库、去重和下载

## 5. Related fixes

- [x] 5.1 上传附件全文注入，不截断
- [x] 5.2 BPMN XML 解析前去除 BOM/前导空白
