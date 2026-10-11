CREATE TABLE governance.ems_database_connection_config (
  config_id uuid PRIMARY KEY,
  name varchar(100) NOT NULL UNIQUE,
  jdbc_url varchar(500) NOT NULL,
  username varchar(200) NOT NULL,
  password_ciphertext text NOT NULL,
  connect_timeout_seconds integer NOT NULL DEFAULT 5 CHECK (connect_timeout_seconds BETWEEN 1 AND 30),
  query_timeout_seconds integer NOT NULL DEFAULT 10 CHECK (query_timeout_seconds BETWEEN 1 AND 60),
  max_rows integer NOT NULL DEFAULT 20 CHECK (max_rows BETWEEN 1 AND 100),
  enabled boolean NOT NULL DEFAULT false,
  created_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_ems_database_connection_config_enabled
  ON governance.ems_database_connection_config ((enabled)) WHERE enabled;
