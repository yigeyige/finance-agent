-- 新库仅启用平台管理员与平台普通用户；已有旧角色数据时失败关闭，避免静默提权。
ALTER TABLE iam.role_binding DROP CONSTRAINT role_binding_role_code_check;
ALTER TABLE iam.role_binding ADD CONSTRAINT role_binding_role_code_check
  CHECK (role_code IN ('PLATFORM_ADMIN', 'PLATFORM_USER'));
