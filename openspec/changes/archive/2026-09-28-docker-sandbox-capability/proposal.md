## Why

The project needs an explicit boundary for Docker-backed filesystem sandboxing. JAR/local may use a tightly constrained `DockerFilesystemSpec` with synthetic input, while the application image must not require a Docker socket or host mounts.

## What Changes

- Add a declarative Docker filesystem spec for JAR/local with network, environment, mount, CPU, memory and timeout limits.
- Add an application-container disabled profile and capability probe that reports why Docker socket access is unavailable there.
- Add a deterministic synthetic calculation and static security contract tests; actual Docker positive/negative execution remains a user-run verification.

## Impact

- New Harness sandbox contract classes and tests only; no Docker client dependency, compose file or runtime startup.
