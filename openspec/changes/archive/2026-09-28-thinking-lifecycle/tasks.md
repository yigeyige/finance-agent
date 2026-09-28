## 1. Thinking lifecycle primitives

- [x] 1.1 Add the volatile reasoning context with close-on-complete/error/cancel semantics.
- [x] 1.2 Add recursive response/event/persistence filtering for forbidden reasoning fields.

## 2. DeepSeek protocol integration

- [x] 2.1 Set default thinking-high options and keep reasoning only in the active tool loop.
- [x] 2.2 Ensure adapter errors/cancellation do not fallback or leak reasoning into model metadata.

## 3. Contract verification

- [x] 3.1 Add HTTP Stub, tool-loop, lifecycle and unique-marker scanning tests; run targeted tests, Maven verify, Surefire scan and strict OpenSpec validation.
