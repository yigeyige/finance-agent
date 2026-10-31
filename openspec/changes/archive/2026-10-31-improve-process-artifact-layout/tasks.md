## 1. Layered layout

- [x] 1.1 在 EmsProcessArtifactConverter 实现分层布局、节点尺寸、纵向分布和连线 dockers
- [x] 1.2 增加简单流程与分支流程布局测试，断言节点不重叠、分支上下分开

## 2. ELK dependency

- [x] 2.1 backend/pom.xml 增加 ELK core/layered 依赖并固定版本
- [x] 2.2 docs/dependency-baseline.md 记录版本、许可证和预留用途

## 3. Required attachment and BPMN projection

- [x] 3.1 SceneSkillAttachment 领域/Repository/DTO 增加 required 字段
- [x] 3.2 新增 BpmnStructureProjector，输出节点/流转/条件和坐标骨架
- [x] 3.3 调查阶段自动注入必读 BPMN 结构投影，非 XML 必读附件按正文注入

## 4. File generation BPMN reference

- [x] 4.1 ProcessArtifactModelContext 增加 bpmnReference（100KB 上限）
- [x] 4.2 ProcessArtifactService 读取必读 XML 原文并注入，超限明确失败

## 5. Prompt SQL

- [x] 5.1 新增 Flyway V32：必读字段、BPMN 必读、GUIDE_HANDLING/场景提示词更新

## 6. Verification

- [x] 6.1 后端编译与相关测试通过
- [ ] 6.2 验证 V32 迁移和必读附件注入，检查布局输出
