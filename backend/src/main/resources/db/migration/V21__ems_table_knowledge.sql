CREATE TABLE governance.ems_table_knowledge (
  config_id uuid NOT NULL REFERENCES governance.ems_database_connection_config(config_id),
  schema_name varchar(100) NOT NULL,
  table_name varchar(128) NOT NULL,
  table_comment varchar(500),
  data_status varchar(10) NOT NULL CHECK (data_status IN ('DATA', 'EMPTY')),
  knowledge jsonb NOT NULL,
  created_by uuid REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid REFERENCES iam.agent_user(user_id),
  updated_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (config_id, schema_name, table_name)
);

ALTER TABLE task.business_scene_skill
  ADD COLUMN database_config_id uuid REFERENCES governance.ems_database_connection_config(config_id);
