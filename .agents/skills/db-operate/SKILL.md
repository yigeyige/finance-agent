---
name: db-operate
description: 当用户提到"数据库、查询表、查表结构、查表字段、表关联关系、外键、执行查询、查数据"等与数据库相关的信息时使用。通过 dbx MCP 连接查询。即使用户没明说 dbx 或数据库，只要想查询本项目的表结构、字段、表关系或执行只读查询也触发。
---

# FCRS 数据库查询

通过 dbx MCP 查询ems-财务系统-本地简易版。所有查询都是**只读**，不写库。
通过 dbx MCP 操作finance-agent-postgresql-local。

## 前置：加载 dbx MCP

先调用 `skill` 工具加载 `dbx`，拿到工具（`dbx_list_tables` / `dbx_describe_table` / `dbx_execute_query` / `dbx_get_schema_context` 等）。

## 连接信息（固定）

- connection_name：`ems-财务系统-本地简易版`
- connection_name：`finance-agent-postgresql-local`

## 库（database）的选择

- 无法确定库时，先用 `dbx_execute_query` 查 `SELECT schema_name FROM information_schema.schemata ORDER BY schema_name` 列出可用库，或直接询问用户。

## 操作

### 1. 查表结构

用 `dbx_describe_table`，参数带上 `connection_name`、`table`、`database`（默认 fcrs）。返回列名、类型、注释/说明，按表格或简洁列表呈现。

### 2. 查表关联关系

按以下优先级判定，不要一上来就乱猜：

1. **外键约束优先**：查 `information_schema` 里的外键约束。典型 SQL：
   ```sql
   SELECT
     tc.constraint_name,
     kcu.table_name,
     kcu.column_name,
     kcu.referenced_table_name,
     kcu.referenced_column_name
   FROM information_schema.KEY_COLUMN_USAGE kcu
   JOIN information_schema.TABLE_CONSTRAINTS tc
     ON tc.constraint_name = kcu.constraint_name
    AND tc.constraint_schema = kcu.constraint_schema
   WHERE kcu.referenced_table_name IS NOT NULL
     AND kcu.constraint_schema = '<database>'
     AND kcu.table_name IN ('<表1>', '<表2>');
   ```
2. **用户显式告知的关系为准**：用户说哪张表和哪张表怎么关联，直接采用，不要质疑或另猜。
3. **未告知时，才做字段名自动推断**：根据 `xxx_id`、`xxx_code` 等字段命名推断可能关联的表，并明确说明"这是推断，未经验证"。

### 3. 执行 SQL（只读）

用 `dbx_execute_query`，只允许 `SELECT` / `SHOW` / `DESCRIBE` / `EXPLAIN` 等只读语句。

- 遇到 `UPDATE` / `DELETE` / `INSERT` / `DROP` / `ALTER` 等写操作，拒绝执行并说明原因。
- **返回行数**：用户未指定行数时，默认 `LIMIT 10`；用户明确指定 N 行时用 `LIMIT N`。
- dbx 单次上限 100 行：若用户要超过 100 行，提示上限，建议加 `WHERE` 缩小范围或分页。
- 结果用表格呈现；字段含义有疑问时，可先 `dbx_describe_table` 看列注释再解读。

## 输出

- 表结构：列名 + 类型 + 注释的表格或列表。
- 关联关系：明确给出"外键约束 / 用户告知 / 字段推断"的判定来源。
- 查询数据：结果表格 + 一行文字说明返回了多少行（是否被 LIMIT 截断）。

## 注意事项

- ems-财务系统-本地简易版 数据库只读，绝不写库。
- 表名、字段名不确定时，先查（`dbx_list_tables` / `dbx_describe_table`）再回答，不要臆造。
