CREATE TABLE governance.model_connection_config (
  config_id uuid PRIMARY KEY,
  name varchar(100) NOT NULL UNIQUE,
  provider varchar(30) NOT NULL CHECK (provider IN ('DEEPSEEK')),
  api_url varchar(500) NOT NULL,
  model_name varchar(100) NOT NULL,
  reasoning_effort varchar(30),
  api_key_ciphertext text NOT NULL,
  enabled boolean NOT NULL DEFAULT false,
  created_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_model_connection_config_enabled
  ON governance.model_connection_config ((enabled)) WHERE enabled;

CREATE TABLE task.task_business_scene_skill (
  task_id uuid PRIMARY KEY REFERENCES task.investigation_task(task_id),
  skill_id uuid NOT NULL REFERENCES task.business_scene_skill(skill_id),
  created_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_task_business_scene_skill_skill ON task.task_business_scene_skill(skill_id);
