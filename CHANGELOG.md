# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive example files demonstrating all major features
- Input validation with helpful error messages
- Integration test suite with 10+ test cases
- Expanded README with prerequisites, installation, and troubleshooting
- LICENSE file (MIT)
- CONTRIBUTING.md with development guidelines
- CHANGELOG.md for tracking version history

### Fixed
- LaTeX backslash rendering in documentation
- Parser delimiter validation
- Function argument count validation

## [2.0.0] - 2024

### Added
- Java implementation (rewrite from OCaml)
- Maven build system
- Modular architecture with separate lexer, parser, AST, and transpiler packages
- Custom exception hierarchy for better error handling
- Token-based lexer with comprehensive token types
- Recursive descent parser with operator precedence
- AST visitor pattern for transpilation
- ValidationHelper utility for input validation

### Changed
- Complete rewrite from OCaml to Java
- Improved error messages with context
- Enhanced operator precedence handling

### Removed
- OCaml implementation (moved to `archive/v1-ocaml/`)

## [1.0.0] - Initial Release

### Added
- OCaml implementation
- Basic lexer and token system
- Simple transpiler for LaTeX expressions
- REPL for interactive testing
- Core mathematical operators (+, -, *, /, ^)
- Basic functions (sqrt, sin, cos, etc.)
- Initial documentation
