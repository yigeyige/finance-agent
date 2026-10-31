CREATE TABLE knowledge.ems_instance_schema_snapshot (
  config_id uuid PRIMARY KEY REFERENCES governance.ems_database_connection_config(config_id),
  catalog_json jsonb NOT NULL,
  table_count integer NOT NULL CHECK (table_count >= 0),
  synced_at timestamptz NOT NULL
);

COMMENT ON TABLE knowledge.ems_instance_schema_snapshot IS '数据库实例配置结构目录最近一次成功同步快照';
COMMENT ON COLUMN knowledge.ems_instance_schema_snapshot.catalog_json IS '仅含表和字段元数据的结构快照';
COMMENT ON COLUMN knowledge.ems_instance_schema_snapshot.synced_at IS '最近一次成功同步时间';

UPDATE governance.prompt_template
SET content = REPLACE(content, '最多 5 张', '最多为输入 maxTables 指定值'), updated_at = now()
WHERE template_code = 'SELECT_TABLES';
