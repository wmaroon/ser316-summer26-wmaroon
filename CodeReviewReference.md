# Code Review Reference — Identified Issues

This document lists quality issues and design concerns identified in the initial codebase.  
The goal is to understand common code smells, design weaknesses, and maintainability risks.

Issues are grouped by category and sorted by structural impact.

---

# 1. Design & API Consistency Issues

## 1.1 Inconsistent Method Signatures (High Impact)

**Location:** `Checkout.java`

- `checkoutBook` accepts a `Book` object.
- `returnBook` accepts an `ISBN` (`String`).

This inconsistency introduces asymmetry in the API (method) and increases coupling to string identifiers.  
A consistent object-oriented interface would improve clarity and type safety.

---

## 1.2 Broken Encapsulation via Setter

**Location:** `Book.java`

- `setAvailableCopies(int)` allows unrestricted external modification of internal state.

This exposes internal representation and bypasses validation rules.  
State-changing operations should ideally be controlled through domain-specific methods.

---

## 1.3 Unused Field (Dead State)

**Location:** `Book.java`

- Field `available` exists but is not meaningfully used.
- `isAvailable()` computes availability using `availableCopies` instead.

This creates redundant state and risk of inconsistency.

---

# 2. Code Quality & Maintainability Issues

## 2.1 Magic Numbers

**Location:** `Checkout.java`

Literal values such as:

- `3`
- `10.0`
- `25.0`

These numbers encode business rules but lack semantic meaning.  
Replacing them with named constants would improve readability and maintainability.

---

## 2.2 Duplicate Business Rules

Some validation and rule logic appears repeated or hard-coded in multiple locations.  
This increases maintenance cost and risk of divergence if policies change.

---

## 2.3 Missing Input Validation

Certain methods assume non-null, valid parameters (e.g., ISBN values or book references).  
Missing validation increases risk of runtime errors.

---

# 3. Structural Complexity

## 3.1 Broad Scope of Responsibilities

**Location:** `Checkout.java`

The `Checkout` class appears to:

- Manage the library’s book collection  
- Interact with patron-related data  
- Enforce borrowing limits  
- Calculate fines  
- Perform checkout and return operations  

This suggests that the class may be responsible for several different concerns within the system.


## 3.2 Long / Multi-Responsibility Methods

Some methods combine:

- validation  
- state mutation  
- fine calculation  
- output formatting  

This increases cyclomatic complexity and reduces readability.

---

# 4. Object-Oriented Design Concerns

## 4.1 Primitive Obsession

Business concepts (e.g., ISBN) are represented as raw `String` values instead of dedicated types.

This reduces type safety and increases error potential.

---

## 4.2 Potential Feature Envy

Some logic that operates heavily on `Book` data resides outside of `Book`, which may suggest misplaced responsibility.

---

# 5. Testability & Evolution Risks

## 5.1 Hard-Coded Policy Rules

Fine limits and borrowing limits are embedded directly in code.

This makes:

- Policy changes harder  
- Configuration impossible without code modification  

---

## 5.2 Tight Coupling of Responsibilities

Business logic, validation, and state management are closely coupled.

This reduces flexibility for:

- Future feature extensions  
- Policy changes  
- Isolated testing  

---

# 6. Minor Clean Code Issues

- Inconsistent naming style in some methods  
- Limited documentation explaining business rule rationale  
- Some methods could benefit from clearer intent-revealing names  

---

# Purpose of This Document

This list is not exhaustive and not all issues require fixing.  

The goal is to:

- Practice identifying code smells  
- Improve design quality incrementally  
- Preserve behavior while improving structure  
