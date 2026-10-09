# Runtime configuration validation Specification

## Purpose

为运行时配置提供语义约束和生产环境的显式安全模式校验。

## Requirements

### Requirement: Runtime diagnostic configuration is semantically validated
The system SHALL reject non-positive diagnostic timeout or budget step/token values and negative budget cost during configuration binding.

#### Scenario: Invalid timeout is supplied
- **WHEN** a diagnostic timeout is zero or negative
- **THEN** configuration validation fails before the invalid value is used by a probe

### Requirement: Production configuration uses explicit safe modes
The system SHALL allow only the supported production identity and storage modes, SHALL reject a source stub, SHALL reject missing required production configuration without including configured values in the failure message, and SHALL require a valid deployment model encryption master key when live model mode is enabled. Production startup MUST NOT require legacy model API URL, API key or model name environment values.

#### Scenario: Unknown production mode is configured
- **WHEN** production configuration specifies an unsupported identity or storage mode
- **THEN** application startup validation fails with the configuration key and without the supplied value

#### Scenario: Production source stub is enabled
- **WHEN** production configuration enables the source stub
- **THEN** application startup validation fails with the source stub configuration key

#### Scenario: Production model master key is absent
- **WHEN** production enables live model mode without a valid deployment model encryption master key
- **THEN** application startup validation fails with the master-key configuration key and does not disclose configuration values

#### Scenario: Legacy model connection variables are absent
- **WHEN** production supplies the required infrastructure configuration and deployment model encryption master key but omits legacy DeepSeek connection variables
- **THEN** application startup validation succeeds
