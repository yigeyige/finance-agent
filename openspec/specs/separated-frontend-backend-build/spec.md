# separated-frontend-backend-build Specification

## Purpose

定义前端静态产物与后端 Spring Boot JAR 的独立构建边界，以及同版本制品的可追溯性。

## Requirements

### Requirement: Frontend and backend artifacts are independently buildable
The build SHALL produce an independently deployable Vue static artifact and a Spring Boot backend JAR from the same `project.version`; the backend JAR SHALL NOT package frontend files.

#### Scenario: Separate artifacts are produced
- **WHEN** the documented artifact build completes
- **THEN** `frontend/dist/index.html` exists, the backend JAR exists, and both artifact records use the same version value

#### Scenario: Backend JAR excludes frontend assets
- **WHEN** the backend JAR contents are inspected
- **THEN** `BOOT-INF/classes/static/index.html` is absent and the JAR remains executable as an API/Actuator service

#### Scenario: Frontend build failure fails closed
- **WHEN** frontend dependency installation or production build fails
- **THEN** the build script exits non-zero and does not claim a complete separated release

### Requirement: Artifact build output is traceable
The build script SHALL print the project version, frontend artifact digest, backend JAR digest, and the image tags when image builds are requested.

#### Scenario: Build evidence is recorded
- **WHEN** the script completes successfully
- **THEN** a reviewer can identify the version and independently verify both artifact hashes without inspecting credentials
