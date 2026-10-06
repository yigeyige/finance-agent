## ADDED Requirements

### Requirement: Publication input is prepared from evidence
页面 SHALL 在草稿组装时保存批准基线，并在发布前重新读取当前证据，自动生成来源版本、覆盖摘要、基线和当前范围快照。业务人员只需确认发布，MUST NOT 手工填写发布 JSON 或快照数组。

#### Scenario: Publish unchanged evidence
- **WHEN** 复核已批准且发布前证据与草稿基线一致
- **THEN** 页面自动提交重查数据，后端完成原子发布并返回发布结果

#### Scenario: Evidence changed before publication
- **WHEN** 发布前重新读取的证据与草稿基线不同
- **THEN** 页面提交真实当前快照，由后端阻断发布并用业务语言展示差异
