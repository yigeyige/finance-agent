## 1. Stable contracts

- [x] 1.1 Add `ApprovedQuery`, `ScopePage`, `RecheckResult` and source adapter error code contracts.
- [x] 1.2 Extend `ConfigSourceAdapter` with the stable read-only SPI while preserving synthetic compatibility.

## 2. Placeholder and TCK

- [x] 2.1 Implement prod-profile API/read-only-view placeholder with fail-closed error behavior.
- [x] 2.2 Add adapter contract tests for synthetic success, invalid input and production no-fallback behavior.

## 3. Verification

- [x] 3.1 Run targeted tests, Maven verify and strict OpenSpec validation.
