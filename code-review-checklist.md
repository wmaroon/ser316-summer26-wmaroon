# Code Review Checklist

**Reviewer Name:** William Maroon
**Date:** June 2, 2026
**Branch:** Blackbox

## Instructions
Review ALL source files (in main not test) in the project and identify defects using the categories below. Log at least 5 defects total:
- At least 1 from CS (Coding Standards)
- At least 1 from CG (Code Quality/General)
- At least 1 from FD (Functional Defects)
- Remaining can be from any category

## Review Categories

- **CS**: Coding Standards (naming conventions, formatting, style violations)
- **CG**: Code Quality/General (design issues, code smells, maintainability)
- **FD**: Functional Defects (logic errors, incorrect behavior, bugs)
- **MD**: Miscellaneous (documentation, comments, other issues)

## Defect Log

| Defect ID | File | Line(s) | Category | Description | Severity |
|-----------|------|---------|----------|-------------|----------|
| 1 | Book.java | 1-2 | CS | `java.util.ArrayList` and `java.util.List` aren't used | L |
| 2 | Patron.java | 117-124 | CS | `getLoanPeriodDays()` hard to read without if-else statements with braces | M |
| 3 | Checkout.java | 16 | CG | Misleading variable name `bookList` - variable is a `Map<String, Book>` not a List; name violates Java naming conventions | M |
| 4 | Patron.java | 130 - 132 | CG | `chkSuspended()` same as `isAccountSuspended()` | M |
| 5 | Book.java | 106 | FD | Logic error: `returnBook()` won't let books with more than 100 copies avaible be checked out | H |
| 6 | Book.java | 112 - 119 | MD | No javadoc comments on `resetAvailability()` and `checkAvailability()` | L |
| 7 | Checkout.java | 17 | MD | `// ` for transaction history field lacks description | L |
| 8 | Patron.java | 151-152 | CG | Empty else block in `addFine()` method suggests incomplete logic | L |
| 9 | Checkout.java | 129 | CS | The constructor Checkout.Transaction(Patron, Book, LocalDate, LocalDate) is never used locally | L |
| 10 | Book.java | 137 | CG | Hash magic number in plain text published to code | C |

**Severity Levels:**
- **Critical**: Causes system failure, data corruption, or security issues
- **High**: Major functional defect or significant quality issue
- **Medium**: Moderate issue affecting maintainability or minor functional problem
- **Low**: Minor style issue or cosmetic problem

## Example Entry

| Defect ID | File          | Line(s) | Category | Description                                | Severity |
|-----------|---------------|---------|----------|--------------------------------------------|----------|
| 1 | Checkout.java | 17      | CS       | Variable bookList misleading - Map not List | Medium |
| 2 | Book.java     | 107     | FD       | Magic number 100 should be totalCopies      | High |

## Notes
- Be specific with line numbers
- Provide clear, actionable descriptions
- Consider: readability, maintainability, correctness, performance, security
- Focus on issues that impact code quality or functionality
