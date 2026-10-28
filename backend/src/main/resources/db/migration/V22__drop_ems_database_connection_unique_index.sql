-- 去掉 EMS 数据库连接配置的唯一启用约束，允许同时启用多个数据库实例。
DROP INDEX IF EXISTS governance.uq_ems_database_connection_config_enabled;
