CREATE TABLE task.business_scene_skill (
  skill_id uuid PRIMARY KEY,
  name varchar(100) NOT NULL,
  description varchar(1000) NOT NULL,
  handling_type varchar(30) NOT NULL CHECK (handling_type IN ('INVESTIGATE', 'ADJUST')),
  premise_type varchar(100) NOT NULL,
  created_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_by uuid NOT NULL REFERENCES iam.agent_user(user_id),
  updated_at timestamptz NOT NULL DEFAULT now(),
  deleted_by uuid REFERENCES iam.agent_user(user_id),
  deleted_at timestamptz
);

CREATE INDEX idx_business_scene_skill_active_updated
  ON task.business_scene_skill(updated_at DESC)
  WHERE deleted_at IS NULL;
