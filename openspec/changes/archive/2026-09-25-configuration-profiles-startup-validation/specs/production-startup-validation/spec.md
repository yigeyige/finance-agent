## ADDED Requirements

### Requirement: Production startup rejects unsafe or incomplete configuration
The system SHALL fail startup for the `prod` profile when any required database, DeepSeek, or workspace setting is missing, when Stub mode is enabled, when identity mode is test/stub, when storage mode is local/memory, or when strict field policy is disabled.

#### Scenario: Missing production configuration fails clearly
- **WHEN** the `prod` profile starts without one or more required configuration values
- **THEN** startup fails with an `IllegalStateException` (or equivalent startup failure) naming the missing properties without revealing their values

#### Scenario: Unsafe production mode fails
- **WHEN** the `prod` profile enables a Stub model, test identity, local/memory storage, or a non-strict field policy
- **THEN** startup fails before serving requests and identifies the invalid property names without logging secrets

#### Scenario: Valid production configuration starts validation successfully
- **WHEN** all required production values are present and production-safe modes are selected
- **THEN** the startup validator completes without throwing

### Requirement: Non-production profiles permit development doubles
The system SHALL allow the `local` and `test` profiles to use Stub model settings and non-production storage/identity defaults without invoking production rejection rules.

#### Scenario: Local Stub mode remains usable
- **WHEN** the `local` profile uses its Stub model and local storage defaults
- **THEN** profile configuration loads without a production validation failure
