## Context

No DockerFilesystemSpec API is present in the locked dependencies, and project deployment explicitly avoids Compose and Docker socket mounts. This slice therefore models the approved contract and probe without starting Docker or binding a host socket.

## Decisions

1. **Two profiles.** `JAR_LOCAL` returns an enabled spec with network disabled, empty mounts, a small non-sensitive environment allowlist and bounded CPU/memory/time. `APPLICATION_CONTAINER` returns disabled and never asks for a socket.
2. **No implicit host access.** The spec rejects network access, arbitrary environment variables, mounts and socket flags. The probe reports `AVAILABLE` only when a caller supplies a local Docker capability result; it never starts a daemon.
3. **Synthetic computation.** A pure arithmetic helper demonstrates sandbox input/output without reading files, credentials, database or network.
4. **Deployment proof.** Tests inspect the backend/frontend Dockerfiles and repository deployment files for non-root users, health checks, no `docker.sock` and no Compose requirement.

## Verification

- Spec profile and limit tests.
- Capability probe and synthetic computation tests.
- Static Dockerfile/socket/compose contract tests; Maven verify and strict OpenSpec validation.
