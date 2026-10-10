CREATE TABLE iam.user_password (
  user_id uuid PRIMARY KEY REFERENCES iam.agent_user(user_id),
  password_hash bytea NOT NULL,
  password_salt bytea NOT NULL,
  iterations integer NOT NULL CHECK (iterations >= 100000),
  algorithm varchar(40) NOT NULL,
  updated_at timestamptz NOT NULL DEFAULT now()
);

INSERT INTO iam.agent_user (user_id, auth_subject, employee_no, display_name, status)
VALUES ('00000000-0000-4000-8000-000000000004', 'admin', 'ADMIN', '平台管理员', 'ACTIVE')
ON CONFLICT (auth_subject) DO NOTHING;

INSERT INTO iam.role_binding (user_id, role_code, granted_by)
SELECT user_id, 'PLATFORM_ADMIN', user_id FROM iam.agent_user
WHERE auth_subject = 'admin' AND NOT EXISTS (
  SELECT 1 FROM iam.role_binding binding WHERE binding.user_id = iam.agent_user.user_id
    AND binding.role_code = 'PLATFORM_ADMIN' AND binding.revoked_at IS NULL
);

INSERT INTO iam.user_password (user_id, password_hash, password_salt, iterations, algorithm)
SELECT user_id, decode('6d1519fea78adc1b9165af2a2b4b1e9687e9c5d6294db2ebf25337525cdebe16', 'hex'),
  decode('758f398973801adcff002fda3904d35c', 'hex'), 310000, 'PBKDF2WithHmacSHA256'
FROM iam.agent_user WHERE auth_subject = 'admin'
ON CONFLICT (user_id) DO NOTHING;

COMMENT ON TABLE iam.user_password IS '用户密码凭据';
COMMENT ON COLUMN iam.user_password.user_id IS '用户标识';
COMMENT ON COLUMN iam.user_password.password_hash IS 'PBKDF2密码散列';
COMMENT ON COLUMN iam.user_password.password_salt IS '密码散列随机盐';
COMMENT ON COLUMN iam.user_password.iterations IS '密码散列迭代次数';
COMMENT ON COLUMN iam.user_password.algorithm IS '密码散列算法';
COMMENT ON COLUMN iam.user_password.updated_at IS '凭据更新时间';
