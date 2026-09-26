CREATE TABLE iam.agent_user (
  user_id uuid PRIMARY KEY,
  auth_subject varchar(200) NOT NULL UNIQUE,
  employee_no varchar(100) NOT NULL UNIQUE,
  display_name varchar(200) NOT NULL,
  status varchar(20) NOT NULL CHECK (status IN ('ACTIVE','DISABLED')),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE iam.role_binding (
  user_id uuid NOT NULL REFERENCES iam.agent_user(user_id),
  role_code varchar(30) NOT NULL CHECK (role_code IN ('OPERATOR','VIEWER','PLATFORM_ADMIN')),
  granted_by uuid REFERENCES iam.agent_user(user_id),
  granted_at timestamptz NOT NULL DEFAULT now(),
  revoked_at timestamptz,
  PRIMARY KEY (user_id, role_code, granted_at)
);

CREATE TABLE governance.runtime_bundle (
  runtime_bundle_id uuid PRIMARY KEY,
  application_version varchar(80) NOT NULL,
  agentscope_version varchar(40) NOT NULL,
  model_binding_version varchar(80) NOT NULL,
  prompt_bundle_version varchar(80) NOT NULL,
  rule_set_version varchar(80) NOT NULL,
  tool_catalog_version varchar(80) NOT NULL,
  source_catalog_version varchar(80) NOT NULL,
  knowledge_release_id uuid,
  bundle_digest char(64) NOT NULL UNIQUE,
  status varchar(20) NOT NULL CHECK (status IN ('DRAFT','TESTED','PUBLISHED','RETIRED')),
  created_at timestamptz NOT NULL DEFAULT now()
);
