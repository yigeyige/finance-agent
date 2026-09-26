## ADDED Requirements

### Requirement: Runtime diagnostic configuration is semantically validated
The system SHALL reject non-positive diagnostic timeout or budget step/token values and negative budget cost during configuration binding.

#### Scenario: Invalid timeout is supplied
- **WHEN** a diagnostic timeout is zero or negative
- **THEN** configuration validation fails before the invalid value is used by a probe

### Requirement: Production configuration uses explicit safe modes
The system SHALL allow only the supported production identity and storage modes, SHALL reject a source stub, and SHALL reject missing required production configuration without including configured values in the failure message.

#### Scenario: Unknown production mode is configured
- **WHEN** production configuration specifies an unsupported identity or storage mode
- **THEN** application startup validation fails with the configuration key and without the supplied value

#### Scenario: Production source stub is enabled
- **WHEN** production configuration enables the source stub
- **THEN** application startup validation fails with the source stub configuration key
