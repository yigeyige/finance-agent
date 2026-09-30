## 1. 脚本兼容性修复

- [x] 1.1 将纵向测试脚本以 UTF-8 BOM 保存，使 Windows PowerShell 5.1 可直接解析。
- [x] 1.2 增加跨 PowerShell 宿主的 HTTP 响应内容读取，并让健康检查保持匿名，保留预期非 2xx 断言。
- [x] 1.3 对 ASSIGN_UUID 字符串主键仓储的 `selectById` 统一使用既有 `PersistenceUuid.toStorage` 映射。
- [x] 1.4 注册 PostgreSQL UUID TypeHandler，覆盖非主键 UUID 参数与结果映射。
- [x] 1.5 为未知 API 异常补充 traceId 关联的安全结构化遥测。
- [x] 1.6 修正重复的 Flyway V10 迁移编号，确保本地库可按唯一版本执行审计列迁移。
- [x] 1.7 统一审计事件摘要时间为 PostgreSQL 微秒精度，避免持久化重读后摘要链误判。
- [x] 1.8 让 HTTP 嵌套 JSON 请求使用 Spring Boot 4 的 Jackson 3 类型，并输出受限根因分类遥测。

## 2. 验证与收尾

- [x] 2.1 新增契约测试，验证 BOM、兼容处理和离线默认边界。
- [x] 2.2 运行 Windows PowerShell 与 PowerShell 7 解析检查、JUnit、Maven 编译和 OpenSpec 严格校验。
- [x] 2.3 增加 UUID 主键查询映射回归测试，并运行相关 Maven 验证和 OpenSpec 严格校验。
- [x] 2.4 增加 UUID TypeHandler 参数绑定与注册回归测试，并运行相关 Maven 验证和 OpenSpec 严格校验。
- [x] 2.5 增加未知异常 traceId 关联与安全响应回归测试，并运行相关 Maven 验证和 OpenSpec 严格校验。
- [x] 2.6 增加审计时间精度回归测试，并运行相关 Maven 验证和 OpenSpec 严格校验。
- [x] 2.7 运行范围和决策包嵌套 JSON、受限根因分类回归验证，并复跑纵向切片；更新任务索引和执行记录。
