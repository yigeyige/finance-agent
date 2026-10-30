## 1. 持久化与迁移

- [ ] 1.1 V30 迁移：business_scene_skill 加 process_artifact_prompt 列、新建 task.business_process_artifact 产物表、GUIDE_HANDLING 模板强化两段式引导、场景调查提示词补 sys_value_set
- [ ] 1.2 场景领域 BusinessSceneSkill/请求/响应/前端契约加生成文件提示词字段（可空）
- [ ] 1.3 产物领域 BusinessProcessArtifact + Repository 及 MyBatis 实现

## 2. 中间表示与模型方法

- [ ] 2.1 定义 ProcessDefinition 中间表示 record（nodes/flows/rules/orgTemplate）与严格 fromJson 校验（id 唯一、引用存在、枚举合法）
- [ ] 2.2 BusinessModelClient 新增 generateProcessDefinition 方法与 DeepSeek/Stub/DatabaseConfigured 实现，输入含任务信息、决策包方案与场景生成文件提示词
- [ ] 2.3 无生成文件提示词或无决策包时明确中文报错

## 3. Java 转换器

- [ ] 3.1 构件工厂：UserTask 五类变体（node_type/监听器默认配置）、ExclusiveGateway、SequenceFlow、StartNoneEvent、EndNoneEvent
- [ ] 3.2 通用图组装 + 分层自动布局，生成 BPMN 编辑器 JSON
- [ ] 3.3 节点参数配置文件生成（assignee 展开为 roleCode/getAssigneePath/is_choose/fixedCode 行）
- [ ] 3.4 审批规则与部门审批流文件生成（rules/orgTemplate 为空跳过），任一失败整体不落库

## 4. 服务与接口

- [ ] 4.1 ProcessArtifactService：触发生成（校验已有决策包、覆盖式更新、留档中间表示）、列表、下载
- [ ] 4.2 Controller 与响应契约：POST/GET process-artifacts、GET download

## 5. 前端

- [ ] 5.1 场景编辑弹窗新增「生成文件提示词」textarea（可选）
- [ ] 5.2 WorkbenchHome 决策包下方「生成/重新生成流程文件」按钮（无决策包置灰）与文件卡片下载
- [ ] 5.3 前端产物 API 与类型契约

## 6. 验证

- [ ] 6.1 中间表示校验、转换器、服务测试；前后端编译/类型检查/构建；OpenSpec 严格校验通过
