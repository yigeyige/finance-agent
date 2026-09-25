# 契约源与生成边界

`contracts/` 是前后端共享的唯一契约源，不在 Java 或 Vue 源码中手工复制 API/领域 DTO。

## 目录

- `openapi/`：OpenAPI 3.1 API 源契约。
- `json-schema/`：Draft 2020-12 领域对象和工具边界 schema，每个文件使用语义版本 `$id`。
- `examples/`：与 schema 同名约定绑定的有效/无效 JSON 样例。

## 生成方向

```text
contracts/openapi + contracts/json-schema
             ├──> backend Java DTO/校验边界（后续生成任务）
             └──> frontend/src/api/generated（后续 TypeScript 生成任务）
```

生成目录只能由契约生成命令更新。手写服务、控制器和视图模型可以引用生成类型，但不能成为契约事实源或维护第二份 DTO 定义。

## 校验

在仓库根目录执行：

```text
python scripts/verify-contracts.py
```

Windows PowerShell 可执行：

```powershell
./scripts/verify-contracts.ps1
```

脚本不启动应用，不访问数据库或外部服务；它会同时验证 OpenAPI 基本结构、schema 元数据以及 `*.valid.json` / `*.invalid.json` 样例。
