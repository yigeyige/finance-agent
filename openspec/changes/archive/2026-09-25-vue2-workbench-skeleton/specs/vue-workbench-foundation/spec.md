## ADDED Requirements

### Requirement: Frontend foundation is reproducibly buildable
The system SHALL provide a locked Vue 2.7.16, TypeScript, Vite, Element UI 2.x, and Vue Router 3 frontend project with a production build that completes without real runtime credentials.

#### Scenario: Clean install and production build
- **WHEN** a user runs `npm ci` followed by `npm run build` in `frontend/`
- **THEN** dependencies install from the lock file and Vite emits a production bundle successfully

#### Scenario: Baseline versions remain pinned
- **WHEN** the frontend manifest and lock file are inspected
- **THEN** Vue, Vue Router, Element UI, Vite, TypeScript, Node, and npm versions match the documented baseline without floating ranges

### Requirement: Workbench exposes a minimal Chinese layout and route
The system SHALL render a reusable workbench layout through Vue Router 3, use Element UI as the only UI component library, and provide discoverable Chinese loading, empty, and error states without small English-only hints.

#### Scenario: Default route renders the workbench shell
- **WHEN** the static application is opened at the default route
- **THEN** the page renders the Chinese workbench shell and an empty-state landing view

#### Scenario: UI library remains singular
- **WHEN** frontend dependencies and source imports are inspected
- **THEN** Element UI is the only UI component library and no second component system is introduced

### Requirement: Generated type boundary is explicit
The system SHALL reserve `src/api/generated` for generated API types and keep handwritten transport/view code outside that directory.

#### Scenario: Generated directory is available
- **WHEN** a later contract generation step runs
- **THEN** generated TypeScript output has a documented, stable destination under `src/api/generated`
