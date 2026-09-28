## 1. Effective classification

- [x] 1.1 Implement injected-clock effective classifier with fixed half-open interval and invalid-input handling.
- [x] 1.2 Add fixed-clock tests for current, future, missing time, invalid interval, expiry and timezone independence.

## 2. Source authority and conflict coordination

- [x] 2.1 Extend `SourceCatalog` with object-level authority order while preserving the legacy constructor.
- [x] 2.2 Implement deterministic source conflict coordinator and `UNKNOWN_SOURCE_CONFLICT` result.
- [x] 2.3 Add authority, tie and missing-registration test vectors.

## 3. Verification

- [x] 3.1 Run targeted tests, Maven verify and strict OpenSpec validation.
