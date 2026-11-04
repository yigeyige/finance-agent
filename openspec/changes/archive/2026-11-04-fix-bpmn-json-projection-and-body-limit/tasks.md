## 1. Context limit

- [x] 1.1 `BusinessGuidanceContext` 场景正文上限改为 3，抽常量并注释“1 必读 + 2 请求”。

## 2. Projector

- [x] 2.1 `BpmnStructureProjector` 增加内容嗅探（JSON vs XML）。
- [x] 2.2 实现 Activiti model JSON 投影：节点、流转、条件、坐标；`source` 为空时从节点 `outgoing` 反推。
- [x] 2.3 保留标准 BPMN XML 路径，JSON 解析失败回退 XML。

## 3. Tests

- [x] 3.1 新增 `BusinessGuidanceContextTest`：3 个正文通过、4 个拒绝。
- [x] 3.2 新增 `BpmnStructureProjectorTest`：Activiti JSON 投影、标准 XML 投影。

## 4. Spec

- [x] 4.1 同步 `business-scene-skill-attachment`：总数上限 3、`.xml` 可能是 Activiti model JSON。

## 5. Verification

- [x] 5.1 运行相关后端测试。
- [x] 5.2 重新打包、重启，复跑“测试3号出差流程”，确认不再出现 `sceneAttachmentBodies is invalid` 与 `[Fatal Error]`（第二轮完整链路因模型服务不可用暂缓）。

## 6. Edge dockers

- [x] 6.1 让 BPMN 连线使用 `route()` 首尾点作为 `dockers`，并按路径计算 `bounds`，避免箭头穿过网关/任务中心。
- [x] 6.2 补断言：网关出边连接点必须是菱形边界，不能是中心 `(50,50)`；`ProcessArtifactPlanTest` 通过。
- [x] 6.3 重新生成产物验证：网关出边 `(100,50)`/`(50,100)`，入边 `(0,50)`。

## 7. EMS shape alignment

- [x] 7.1 对照 EMS 样例：网关 40×40，网关连线使用菱形中心 `(20,20)`；任务/事件使用边界点。
- [x] 7.2 更新测试断言（网关尺寸 40、网关出/入边中心 docker）并运行通过。

## 8. Sample alignment

- [x] 8.1 任务 100×80、开始 30×30、结束 28×28；开始/结束事件使用中心 docker；网关名称置空。
- [x] 8.2 测试补充开始事件中心 `(15,15)` 与任务边界断言并通过。
