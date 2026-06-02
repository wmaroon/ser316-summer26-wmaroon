# Library Management System - Gradle Commands

## Basic Commands

```bash
# Run the library demo application
./gradlew run

# Or use the custom task
./gradlew runDemo

# Run all tests
./gradlew test

# Run only Black Box tests (Assignment 2)
./gradlew blackBoxTest

# Run only White Box tests (Assignment 3)
./gradlew whiteBoxTest

# Clean build artifacts
./gradlew clean

# Build the project
./gradlew build
```

## Static Analysis (Assignments 4+)

```bash
# Run Checkstyle
./gradlew checkstyleMain -PstaticAnalysis=true

# Run SpotBugs
./gradlew spotbugsMain -PstaticAnalysis=true

# Run all static analysis checks
./gradlew check -PstaticAnalysis=true
```

## Code Coverage (Assignment 3+)

```bash
# Generate JaCoCo coverage report (runs automatically after tests)
./gradlew test

# View report at: build/reports/jacoco/test/html/index.html
```

## Test Reports

After running tests, view HTML reports at:
- **All tests**: `build/reports/tests/test/index.html`
- **Black Box**: `build/reports/tests/blackBoxTest/index.html`
- **White Box**: `build/reports/tests/whiteBoxTest/index.html`

## Requirements

- Java JDK 18 or higher
- Gradle 8.x (wrapper included)
