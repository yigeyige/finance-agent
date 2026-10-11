## Why

业务处理材料经常以 Excel 交付，现有初稿分析只能接收纯文本附件，导致模型无法利用表格中的业务事实。当前模型客户端是受控文本交互，必须先在服务端解析并截断工作簿内容，而不能直接上传二进制文件。

## What Changes

- 引入用户授权的 `cn.idev.excel:fastexcel`，用于流式读取 `.xls` 与 `.xlsx` 工作簿。
- 将受控附件解析扩展至 Excel，输出含工作表、行列和值边界的临时文本投影。
- 为 Excel 工作簿增加工作表、行、列、单元格和总文本限制；损坏、加密或超限文件安全失败。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `business-handling-attachment-analysis`: 扩展初稿附件支持范围，使 `.xls`、`.xlsx` 在受限流式解析后可作为模型分析材料。

## Impact

- 后端 Maven 新增 FastExcel 1.3.0 依赖，并调整附件解析器及单元测试。
- 前端附件选择白名单增加 `.xls`、`.xlsx`，保持 multipart API 不变。
- 不修改数据库、对象存储和模型输出契约；工作簿及解析文本仍只在请求内存中存在。
