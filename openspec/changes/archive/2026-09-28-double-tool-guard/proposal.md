## Why

工具目录和审批守卫虽然分别存在，但还没有统一调用管线把 Harness 许可、业务绑定、目录版本和幂等意图串联起来，调用方可能绕过其中一层。

## What Changes

增加进程内短期签名 permit、BusinessToolGuard 和 ToolGateway，在任何外部源调用前重验 task/step/approval/scope、目录和幂等边界。

