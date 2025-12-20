# TOON Repair

ANTLR-based repair-on-error pipeline with golden-file tests.

## Overview

TOON Repair is a tool for attempting automatic repair of a given TOON format string.
It provides an ANTLR-based repair-on-error pipeline with a suite of golden-file tests to validate correctness and
behavior.
This project is implemented in Java and leverages ANTLR for parsing and error recovery of TOON strings.

## Features

- ANTLR-based parser for TOON syntax
- Error detection and repair pipeline
- Designed to help recover broken or malformed TOON input

## Requirements

- Java Development Kit (JDK) 17 or newer
- Maven for build and dependency management
- ANTLR toolchain (bundled via Maven)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/jenspapenhagen/toon-repair.git
cd toon-repair
```

### Build

Use Maven to compile the project and generate any ANTLR artifacts:

```bash
mvn clean install
```

This will compile the source code, generate parser code from grammar definitions, and run the included tests.

## Usage

At this time the repository does not include a packaged CLI tool or published artifact. You can integrate the repair
pipeline into your own Java application via the provided classes.

A typical usage flow might be:

1. Parse a TOON string using the ANTLR-generated parser
2. Detect and collect parsing errors
3. Apply repair logic
4. Output a repaired TOON string or detailed diagnostic

Refer to the `src/main/java` sources for examples of how the parser and repair pipeline are invoked.

## Contributing

Contributions are welcome. To contribute:

1. Fork the repository
2. Create a feature branch
3. Implement or fix behavior
4. Add or update tests
5. Submit a pull request

Ensure all tests pass before submitting.

## License

This project is available under the **MIT License**. See the `LICENSE` file for details.