## 1. OpenSpec and task setup

- [x] 1.1 Complete proposal, design, delta specs, and task linkage for DEV-BOOT-005.

## 2. Frontend toolchain

- [x] 2.1 Add `frontend/package.json`, lock file, `.nvmrc`, TypeScript config, and Vite config with pinned versions.
- [x] 2.2 Add Vue 2.7.16 entry point, Element UI registration, base layout, and empty route.
- [x] 2.3 Add centralized Axios HTTP client, native EventSource SSE client, generated-type boundary, and Chinese error utility.
- [x] 2.4 Add EOL/version documentation and confirm no development credentials are included.

## 3. Verification

- [x] 3.1 Run `npm ci` in `frontend/`.
- [x] 3.2 Run `npm run typecheck` and `npm run build`.
- [x] 3.3 Inspect dependency versions, bundle inputs, and task evidence; update task index when complete.
