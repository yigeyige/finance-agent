## 1. Configuration and live boundary

- [x] 1.1 Set DeepSeek defaults and add an explicit manual live-test profile/guide.

## 2. AgentScope adapter

- [x] 2.1 Add a secret-safe DeepSeek OpenAI-compatible adapter using the existing AgentScope extension.
- [x] 2.2 Pass structured output/tool/timeout options and preserve cancellation/error semantics without model fallback.

## 3. Contract verification

- [x] 3.1 Add HTTP Stub and no-key contract tests; run targeted tests, Maven verify, Surefire scan and strict OpenSpec validation.
