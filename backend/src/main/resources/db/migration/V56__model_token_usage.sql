-- 模型 token 用量按真实调用逐条记录，供个人与全平台统计。
-- 不使用外键：用户或任务被清理后，历史用量仍需保留用于成本分析。
CREATE SCHEMA IF NOT EXISTS usage;

CREATE TABLE usage.model_token_usage (
  model_token_usage_id uuid PRIMARY KEY,
  user_id uuid NOT NULL,
  task_id uuid,
  model_name varchar(200) NOT NULL,
  mode varchar(16) NOT NULL,
  tokens bigint NOT NULL CHECK (tokens >= 0),
  occurred_at timestamptz NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT model_token_usage_mode_check CHECK (mode IN ('LIVE', 'STUB'))
);

CREATE INDEX idx_model_token_usage_user_time ON usage.model_token_usage (user_id, occurred_at);
CREATE INDEX idx_model_token_usage_time ON usage.model_token_usage (occurred_at);
