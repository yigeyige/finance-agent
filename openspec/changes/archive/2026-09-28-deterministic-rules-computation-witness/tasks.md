## 1. Rule SPI and normalized input

- [x] 1.1 Add versioned deterministic rule SPI, approved snapshot input and canonical input normalizer using existing safe-field and JSON utilities.
- [x] 1.2 Implement the first personnel-reference rule and deterministic rule catalog.

## 2. Findings, witnesses and recompute

- [x] 2.1 Implement rule evaluation service producing Finding and ComputationWitness without overwriting prior versions.
- [x] 2.2 Implement independent `rules.recompute` service with MATCHED/MISMATCHED/UNAVAILABLE outcomes.
- [x] 2.3 Add golden-vector and negative tests for missing snapshots, unauthorized fields, digest changes and unknown rule versions.

## 3. Verification

- [x] 3.1 Run targeted tests, Maven verify and strict OpenSpec validation.
