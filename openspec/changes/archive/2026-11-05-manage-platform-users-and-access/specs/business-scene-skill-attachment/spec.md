## MODIFIED Requirements

### Requirement: Scene skill attachment persistence
系统 SHALL 允许场景作者或平台管理员为业务场景 Skill 上传并持久化附件，保存文本投影与填写的摘要；每场景最多 5 个附件，单个文件不超过 100KB，仅支持 json、xml、xlsx、txt、md、csv 格式。

#### Scenario: Upload attachment
- **WHEN** 作者或平台管理员为场景上传受支持格式且不超过 100KB 的附件并填写摘要
- **THEN** 系统保存附件文本投影与摘要，供后续模型分析复用

#### Scenario: Reject exceeding limits
- **WHEN** 附件超过 5 个、单个超过 100KB 或摘要为空
- **THEN** 系统拒绝并给出中文说明

## ADDED Requirements

### Requirement: Shared scene visibility and owner maintenance
系统 SHALL 允许两种角色查看、选用全部有效业务场景；PLATFORM_USER 仅可编辑、删除自己创建的场景及维护其附件；PLATFORM_ADMIN 可维护全部场景。停用作者不影响场景可见性与选用。

#### Scenario: Another user's scene
- **WHEN** 普通用户查看或选择他人创建的有效场景
- **THEN** 系统返回场景并允许选择，但拒绝编辑、删除及附件写操作

#### Scenario: Administrator maintains scene
- **WHEN** 管理员维护任意有效场景
- **THEN** 系统允许该操作并保留原创建人
