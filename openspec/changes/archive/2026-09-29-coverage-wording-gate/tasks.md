## 1. 固化场景与规则

- [x] 1.1 新增版本化 fixture，覆盖 COMPLETE/PARTIAL/UNKNOWN、分页变化、冲突、未来转当前、RAG/记忆占位和未确认业务影响。
- [x] 1.2 新增 `PacketWordingValidator` 并编写规则单元测试。

## 2. 提供验收资产

- [x] 2.1 新增离线 PowerShell 校验脚本，核对场景数量、必备限制和拒绝措辞。
- [x] 2.2 新增后端契约测试，校验 fixture、validator 和审阅证据要求。

## 3. 验证和交付

- [x] 3.1 运行目标 JUnit、Maven 编译、契约校验和脚本静态检查。
- [x] 3.2 更新任务记录与索引，严格校验并归档 OpenSpec 变更。
