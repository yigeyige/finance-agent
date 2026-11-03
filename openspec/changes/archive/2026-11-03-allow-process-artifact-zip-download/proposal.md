## Why

模型生成的流程产物通常需要管理员人工补充，即使批次处于 `NEEDS_REPAIR`，产物文件仍然是后续处理的输入。当前前后端只允许 `IMPORT_CANDIDATE` 批次下载 ZIP，导致用户只能逐个下载文件，无法方便地继续人工修复。

## What Changes

- 允许存在流程产物且包含模型文件的批次下载 ZIP，不再以 `importStatus` 限制下载。
- 保留模型文件必选、文件类型白名单、批次存在性和权限校验。
- 前端在待修复批次上启用 ZIP 下载按钮，并继续展示待补信息和状态。
- 增加后端控制器测试，覆盖待修复批次下载和原有非法请求拒绝。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-process-artifact`: 流程产物 ZIP 下载从“仅导入候选”调整为“有模型产物即可下载”。

## Impact

- 前端：`TaskDetail.vue`、流程产物下载按钮状态。
- 后端：`ProcessArtifactController` ZIP 下载状态校验。
- 测试：流程产物 Controller 测试及前端构建检查。
- 不新增数据库结构、依赖或 API 路径。
