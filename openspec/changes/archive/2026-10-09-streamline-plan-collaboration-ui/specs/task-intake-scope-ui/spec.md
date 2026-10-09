## MODIFIED Requirements

### Requirement: Task intake and scope confirmation form
工作台 SHALL 以两步业务处理向导提供任务标题、业务说明、对象前提和范围确认表单，并在每次写请求进行中阻止重复提交。第一步仅要求业务标题和业务说明；创建任务后第二步 SHALL 展示模型预填且可编辑的单一处理对象、信息依据、系统、日期范围、处理内容与数据使用范围。处理方式、业务场景等短字段 MUST 紧凑地并列展示，长文本和多选内容保持整行可读。系统 MUST 用该处理对象映射前提与范围所需的内部对象字段，不得要求业务人员输入 JSON 或重复填写对象。

#### Scenario: Create and confirm handling scope
- **WHEN** OPERATOR 填写业务标题和说明，接受或修改模型初稿，并确认对象和处理范围
- **THEN** 页面按两步推进，系统先创建任务，再用同一处理对象按顺序保存前提和范围修订并显示可理解的业务方案入口

#### Scenario: Task draft creation fails
- **WHEN** 任务草稿创建请求发生网络错误或后端拒绝请求
- **THEN** 页面保留业务标题和说明表单、恢复提交按钮并显示明确中文错误提示

#### Scenario: Duplicate task draft submission is blocked
- **WHEN** 任务草稿创建请求仍在处理中且用户再次触发提交
- **THEN** 页面不发送第二个创建请求

#### Scenario: Incomplete business scope
- **WHEN** 处理对象、日期范围或处理内容未填写完整
- **THEN** 页面阻止确认并用业务语言指出缺失内容，不显示 JSON 格式错误
