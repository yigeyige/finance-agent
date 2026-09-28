## Decisions

1. `SyntheticConfigSourceAdapter` 只接受注册模板 `config.employee.references.v1`，参数白名单为 `page`、`size`，不暴露 SQL/URL/写操作。
2. 场景由枚举选择，数据全部为 `EMP-SYN-001` 等合成标识；同 seed 和模板参数产生稳定结果。
3. 分页失败、跨源冲突、未来引用和未知项通过场景数据显式表达，供后续覆盖度与冲突处理任务复用。

## Non-Goals

- 不接入真实财务源，不访问网络或数据库。

