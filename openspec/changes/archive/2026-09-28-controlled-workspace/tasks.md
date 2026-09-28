## 1. Workspace configuration and path policy

- [x] 1.1 Add workspace quota/media-type settings and local/test profile defaults.
- [x] 1.2 Add server-controlled user/task/session path resolver with real-path and symlink checks.

## 2. Controlled manager

- [x] 2.1 Implement bounded write/read/cleanup APIs with digest metadata and isolated quotas.
- [x] 2.2 Ensure workspace references remain temporary and cannot replace authoritative evidence storage.

## 3. Contract verification

- [x] 3.1 Add path, isolation, symlink, quota, media-type and cleanup tests; run targeted tests, Maven verify, Surefire scan and strict OpenSpec validation.
