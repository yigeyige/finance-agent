## ADDED Requirements

### Requirement: Workspace is isolated by server-controlled identity

The system SHALL generate workspace paths under a configured root using user, task and session identities, SHALL reject caller-supplied absolute or traversal paths, and SHALL not treat workspace content as the sole authoritative evidence copy.

#### Scenario: Same relative path is isolated across tasks and users

- **WHEN** two sessions write the same relative path with different user or task identities
- **THEN** the server resolves two different workspace directories and neither session can resolve the other directory through a relative path

#### Scenario: Path escape is rejected

- **WHEN** a caller supplies an absolute path, `..` traversal, or a symlink that points outside the workspace
- **THEN** the manager rejects the operation before reading or writing the target

### Requirement: Workspace writes enforce quota, type and cleanup boundaries

The system SHALL enforce configured allowed media types, per-file and per-workspace byte quotas, return a digest-bearing temporary artifact, and provide cleanup limited to the complete user/task/session workspace.

#### Scenario: Valid temporary artifact is written and cleaned

- **WHEN** a valid content stream is below quota and uses an allowed media type
- **THEN** the manager writes it below the isolated workspace, returns size and digest metadata, and later cleanup removes only that workspace

#### Scenario: Quota or media type is exceeded

- **WHEN** content exceeds a configured limit or its media type is not allowed
- **THEN** the write fails and no partial artifact remains
