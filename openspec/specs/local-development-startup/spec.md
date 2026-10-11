## Purpose

为 Windows 本地开发提供统一、可重复执行的前后端启动入口，并确保后端子进程获得仓库 `.env` 中的运行变量。

## Requirements

### Requirement: Unified local startup entry

系统 SHALL 在 `scripts/` 中提供一个可由 Windows PowerShell 5.1 和 PowerShell 7 执行的启动脚本。脚本 MUST 从仓库根目录 `.env` 导入有效环境变量；当后端端口未监听时，MUST 在启动后端前使用相同进程环境执行项目 Flyway 迁移。迁移成功后，脚本分别启动监听 8080 的后端和监听 5173 的前端，并在控制台输出本地访问地址；迁移失败时 MUST 不启动新的后端或前端进程。

#### Scenario: Start both local services from a clean state
- **WHEN** 开发者在仓库任意工作目录执行启动脚本，`.env`、Maven、npm 和前端依赖均可用，且 8080 与 5173 均未监听
- **THEN** 脚本将 `.env` 变量仅注入本次命令及所启动的本地进程，先完成 Flyway 迁移，再启动后端与前端，并输出前端地址、后端地址和日志位置

#### Scenario: Migration fails before services start
- **WHEN** 后端端口未监听且 Flyway 迁移命令失败
- **THEN** 脚本以非零状态结束，不创建新的后端或前端进程，且不输出数据库密码或完整连接凭据

### Requirement: Idempotent listener handling

启动脚本 SHALL 在目标端口已经监听时复用现有服务，而 MUST NOT 终止进程或创建第二个同端口实例。后端端口已经监听时，脚本 MUST 不执行迁移，以避免向运行中的本地实例施加数据库变更。

#### Scenario: Re-run while local services are already listening
- **WHEN** 开发者再次执行启动脚本且 8080 和 5173 已被监听
- **THEN** 脚本报告两个服务均被复用，不执行迁移，并成功结束而不启动重复进程

### Requirement: Safe local configuration and logs

启动脚本 MUST 在 `.env` 缺失或存在不合法赋值行时失败关闭，MUST NOT 在控制台、命令行或日志中输出环境变量值。脚本 SHALL 将所启动进程的标准输出与错误输出写入本地运行日志。

#### Scenario: Configuration file is unavailable
- **WHEN** 开发者执行脚本但仓库根目录不存在 `.env`
- **THEN** 脚本以非零状态结束，指出缺失的配置文件路径，且不启动任何服务
