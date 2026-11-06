## 1. 后端模型能力

- [x] 1.1 新增 `SceneSkillPrefillTarget` / `SceneSkillPrefillContext` / `SceneSkillPrefillSuggestion`，在 `BusinessModelClient` 增加抽象方法。
- [x] 1.2 DeepSeek 新增独立 `invokeScenePrefill`（60s）；Stub 确定性返回；DatabaseConfigured 转发；Capturing 以空 taskId 记录 token。

## 2. 表名筛选与预填服务

- [x] 2.1 新增 `SceneSkillTableSelector`：schema 初筛 + 日志/临时/备份/日期/MQ/事务排除，返回使用与排除数量。
- [x] 2.2 新增 `SceneSkillPrefillService`：target 校验、前置提示词阻断、实例校验与表名读取、调用模型、按字段限制截断。
- [x] 2.3 新增 `V58` 提示词模板 `SCENE_SKILL_PREFILL`。

## 3. 接口与契约

- [x] 3.1 新增 `POST /api/v1/business-scene-skills/prefill`（新建 PLATFORM_USER、编辑维护者），映射 400/403/502/504。
- [x] 3.2 OpenAPI 升到 1.7.0，新增请求/响应 schema 与样例。

## 4. 前端改造（第二个任务）

- [x] 4.1 场景新建/编辑改为内容区全页表单，标题右侧「AI 预填」+「返回」，未保存返回需确认。
- [x] 4.2 新增 AI 预填弹窗：文本 + 实例下拉 + 三个生成按钮，按字段预览与应用；调查按钮同时回填名称。
- [x] 4.3 前端 generated 类型与 `services/scene-skill-prefill-api.ts`；调查提示词上限对齐 8000；附件区搬入全页表单。

## 5. 验证

- [x] 5.1 单测：schema 初筛、排除规则、空表兜底、前置缺失阻断、权限、截断。
- [x] 5.2 后端编译、定向测试、契约校验。
- [x] 5.3 前端 typecheck、测试、构建与交互核对。

## 验证记录

- 2026-11-06（后端）：`SceneSkillTableSelectorTest`、`SceneSkillPrefillServiceTest`、`SceneSkillPrefillStubTest` 9 项通过；与 intake/装饰器/迁移契约回归共 8 个测试类通过，后端 `compile` 通过。
- 2026-11-06（契约）：`verify-contracts.py` 通过（33 个 schema、36 个样例），OpenAPI 升至 1.7.0。
- 2026-11-06（前端）：`tsc --noEmit`、47 项测试、`npm run build` 通过；弹窗复用 `v-workbench-dialog`，输入框与预览区随窗口高度伸缩。
- 未验证：V58 尚未在运行库执行；页面与真实模型返回未做联调与视觉走查。
