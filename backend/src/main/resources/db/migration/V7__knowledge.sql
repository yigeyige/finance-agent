CREATE TABLE knowledge.embedding_set (
  embedding_set_id uuid PRIMARY KEY,
  model_version varchar(100) NOT NULL,
  dimensions integer NOT NULL CHECK (dimensions > 0),
  status varchar(20) NOT NULL CHECK (status IN ('BUILDING','VALIDATING','ACTIVE','RETIRED')),
  created_at timestamptz NOT NULL
);

CREATE TABLE knowledge.document (
  document_id uuid PRIMARY KEY,
  title varchar(500) NOT NULL,
  source_ref varchar(1000) NOT NULL,
  document_version varchar(100) NOT NULL,
  applicable_scope jsonb NOT NULL,
  status varchar(20) NOT NULL CHECK (status IN ('UPLOADED','PARSED','REVIEWED','PUBLISHED','RETIRED')),
  approved_by uuid REFERENCES iam.agent_user(user_id),
  approved_at timestamptz,
  content_digest char(64) NOT NULL
);

CREATE TABLE knowledge.chunk (
  chunk_id uuid PRIMARY KEY,
  document_id uuid NOT NULL REFERENCES knowledge.document(document_id),
  embedding_set_id uuid NOT NULL REFERENCES knowledge.embedding_set(embedding_set_id),
  chunk_no integer NOT NULL,
  section_path text,
  page_no integer,
  content text NOT NULL,
  content_digest char(64) NOT NULL,
  embedding vector(3),
  metadata jsonb NOT NULL,
  UNIQUE(document_id, embedding_set_id, chunk_no)
);
