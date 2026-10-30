# business-scene-skill-attachment Specification

## Purpose
TBD - created by archiving change scene-skill-attachments. Update Purpose after archive.

## Requirements

### Requirement: Scene skill attachment persistence
系统 SHALL 允许管理员为业务场景 Skill 上传并持久化附件，保存文本投影与管理员填写的摘要；每场景最多 5 个附件，单个文件不超过 100KB，仅支持 json、xml、xlsx、txt、md、csv 格式。

#### Scenario: Upload attachment
- **WHEN** 管理员为场景上传受支持格式且不超过 100KB 的附件并填写摘要
- **THEN** 系统保存附件文本投影与摘要，供后续模型分析复用

#### Scenario: Reject exceeding limits
- **WHEN** 附件超过 5 个、单个超过 100KB 或摘要为空
- **THEN** 系统拒绝并给出中文说明

### Requirement: Scene attachment used on demand
系统 SHALL 在调查阶段把场景附件清单（文件名+摘要）注入模型第一轮，模型声明需要的附件后，第二轮按需注入至多 2 个附件的完整正文；无附件或模型不请求正文时一轮结束，不得一股脑全传正文。

#### Scenario: Model requests attachment body
- **WHEN** 模型第一轮根据清单声明需要某些附件正文
- **THEN** 系统仅注入被选中且至多 2 个附件的完整正文，进行第二轮分析

#### Scenario: No attachment requested
- **WHEN** 场景无附件或模型不请求正文
- **THEN** 系统一轮返回结果，不注入任何附件正文
