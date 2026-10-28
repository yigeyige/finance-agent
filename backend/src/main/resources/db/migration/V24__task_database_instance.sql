-- 任务可关联数据库实例。
ALTER TABLE task.investigation_task ADD COLUMN database_config_id uuid;

COMMENT ON COLUMN task.investigation_task.database_config_id IS '任务可选关联的数据库实例标识，为空表示不使用数据库数据';
