# 开发命令索引

本文档只记录稳定的命令入口和责任边界。具体任务的验收命令以对应任务卡为准；命令所需的环境和依赖未准备好时，不用默认值或真实凭证绕过检查。

## 仓库检查

```powershell
git status --short
rg --files
rg -n -i "api[_-]?key|password|secret|token|jdbc:[^\s]+:[^\s]+" --glob "!node_modules/**" --glob "!.git/**"
```

敏感信息扫描只允许输出文件路径和必要的匹配位置，不得展示密钥、密码、连接串或真实业务数据。

## 后端

后端工程完成初始化后，在 `backend/` 执行任务卡要求的 Maven 命令：

```powershell
./mvnw -q -DskipTests package
./mvnw -q test
```

依赖版本、AgentScope API 兼容性和架构检查以对应 BOOT/HAR 任务为准。

## 前端

前端工程完成初始化后，在 `frontend/` 执行：

```powershell
npm ci
npm run typecheck
npm run build
```

前端页面应复用既有 Vue 2/Element UI 体系，使用中文业务提示，不引入第二套 UI 组件体系。

## 契约和脚本

契约校验脚本放在 `scripts/`，命名为 `verify-contracts.*`；添加脚本时必须同时补充使用说明和正/负样例。脚本不得读取或打印 `.env`、密钥、账号密码及真实用户数据。

## 启动与发布责任

AI 负责静态检查、编译和任务卡要求的自动化验证。用户负责启动长期运行的 JAR/应用容器、真实 DeepSeek 调用、人工页面验收和实际部署；未获得明确授权时不发布生产环境。
