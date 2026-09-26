## ADDED Requirements

### Requirement: One version produces a runnable JAR with frontend assets
The build SHALL build the Vue workbench before Maven packaging and include `frontend/dist` under the executable JAR's `static/` path, with the JAR version read from `backend/pom.xml`.

#### Scenario: Frontend assets are packaged
- **WHEN** the documented artifact build completes
- **THEN** the executable JAR contains `static/index.html` and the compiled frontend assets without requiring a separate frontend server

#### Scenario: Missing frontend output fails closed
- **WHEN** Maven packaging is attempted without the required frontend `dist` output
- **THEN** the build fails instead of producing a JAR that silently omits the workbench

#### Scenario: Version identity is consistent
- **WHEN** the artifact script builds a JAR and optional image
- **THEN** both use the same `project.version` value from `backend/pom.xml`

### Requirement: Build context excludes credentials and caches
The build SHALL exclude `.env`, secrets, source-control metadata, Node/Maven caches, and generated local artifacts from the Docker build context and final image.

#### Scenario: Final image contains only runtime material
- **WHEN** the multi-stage Docker build completes
- **THEN** the final image contains the runtime JRE, application JAR, and required working directory but no frontend source, npm cache, Maven cache, or `.env`
