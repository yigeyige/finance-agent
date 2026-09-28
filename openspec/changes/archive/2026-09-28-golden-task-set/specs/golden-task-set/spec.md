# Golden task set

## ADDED Requirements

### Requirement: Versioned synthetic golden fixtures
The system SHALL provide versioned synthetic fixtures with a fixed seed covering current/future/unknown classifications, conflicts, pagination failure, complete and partial no-match, and pre-publication new references.

#### Scenario: Same seed is reproducible
- **WHEN** a fixture scenario is queried twice with the same seed and template
- **THEN** records, page metadata and result digest are identical

#### Scenario: Sensitive values are excluded
- **WHEN** the golden manifest and fixture schema are checked
- **THEN** no real personnel, funds, credentials, SQL, prompt or reasoning content is present

### Requirement: Expected output bindings
The system SHALL bind each fixture to explicit source, rule and packet schema versions plus expected coverage, finding, witness and packet sections.

#### Scenario: Rule upgrade requires an explicit version change
- **WHEN** a rule or packet structure changes
- **THEN** the manifest version binding and expected result must be updated explicitly before acceptance
