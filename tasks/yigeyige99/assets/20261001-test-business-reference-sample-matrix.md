# P1 测试业务多类型参考样本矩阵

- 样本版本：`test-business-reference-samples-v1`
- 适用环境：仅 `local` / `test`
- 数据性质：固定虚构测试标识；不包含真实人员、金额、单据、凭证、生产配置或连接信息。
- 适用范围：人员、岗位、角色、流程节点、组织/权限范围的状态边界回归；不代表真实业务源覆盖度。

| 样本 ID | 人员 | 岗位 | 角色 | 流程节点 | 组织/权限范围 | 黄金事实状态 | 边界结论 |
|---|---|---|---|---|---|---|---|
| `TST-REF-001` | `TST-EMP-001` | `TST-POS-ACCOUNTING` | `TEST_OPERATOR` | `TEST_NODE_REVIEW` | `ORG-TEST-A` | `CURRENT` | 当前已确认的测试事实 |
| `TST-REF-002` | `TST-EMP-002` | `TST-POS-ACCOUNTING` | `TEST_VIEWER` | `TEST_NODE_SUBMIT` | `ORG-TEST-A` | `FUTURE` | 未来生效，不作为当前确认事实 |
| `TST-REF-003` | `TST-EMP-003` | `TST-POS-CONTROL` | `TEST_REVIEWER` | `TEST_NODE_REVIEW` | `ORG-TEST-B` | `UNKNOWN` | 保持未知，不能默认确认 |
| `TST-REF-004` | `TST-EMP-404` | `TST-POS-NONE` | `TEST_NO_ROLE` | `TEST_NODE_NONE` | `ORG-TEST-NONE` | `NO_MATCH` | 无匹配，不能回退为单源确认 |

## 黄金事实核对规则

1. 只有 `CURRENT` 可被测试断言为当前确认事实。
2. `FUTURE`、`UNKNOWN` 和 `NO_MATCH` 均为非确认结果；不得用模型、默认值或候选顺序改写。
3. 样本按 `sampleId` 固定读取；不存在的标识返回无样本，不产生替代记录。
4. 新增场景必须新增明确样本与黄金事实，不得以单一“人员离职”或既有案例声称通用覆盖。
