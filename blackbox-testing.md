# Black Box Testing Report - Assignment 2

**Student Name:** William Maroon 
**ASU ID:** wmaroon  
**Date:** June 1 2026

---

## Part 1: Equivalence Partitioning (EP)

Identify equivalence partitions for the `checkoutBook(Book book, Patron patron)` method based on the specification (JavaDoc).

Create **multiple tables**, one per partition category (e.g., book state, patron state, renewal, limits, etc.).

Do **not** put everything into one table.

**Column Explanations:**
- **Partition ID**: Unique identifier (e.g., EP 1.1, EP 2.1)
- **State**: The specific state/value for this partition (e.g., "Unavailable", "Available")
- **Valid/Invalid**: Whether this partition represents valid or invalid input
- **Input Condition**: Precise condition that defines this partition
- **Expected Return**: What return code you expect
- **Expected Behavior**: What should happen

### Example EP Table: Book Availability

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 1.1 | Unavailable (0 copies) | Invalid | availableCopies == 0 AND other conditions allow checkout | 2.0 | No copies to checkout |
| EP 1.2 | Available (1+ copies) | Valid | availableCopies > 0 AND other conditions allow checkout | Success | Book can be checked out |

**Example test cases:** `testBookAvailable()`, `testUnavailableBook()`

---

### EP Table 1: Book Input

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 1.1 | Book is null | Invalid | book == null | 2.1 | Null book rejected, no state change |
| EP 1.2 | Book is reference-only | Valid | book.getType() == REFERENCE | 5.0 | Reference books cannot be checked out |
| EP 1.3 | Book is fiction | Valid | book.getType() == FICTION (or NONFICTION, TEXTBOOK, CHILDREN) | 0.0 - 5.0 | Proceeds to further validation |

### EP Table 2: Book Availability

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 2.1 | Book unavailable | Valid | book.getAvailableCopies() == 0 | 2.0 | All copies checked out, cannot checkout |
| EP 2.2 | Book available (single copy) | Valid | book.getAvailableCopies() == 1 | 0.0 - 1.1 | Single copy available, proceeds based on patron/renewal |
| EP 2.3 | Book available (multiple) | Valid | book.getAvailableCopies() >= 2 | 0.0 - 1.1 | Multiple copies available, proceeds based on patron/renewal |

### EP Table 3: Patron Input

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 3.1 | Patron is null | Invalid | patron == null | 3.1 | Null patron rejected, no state change |

### EP Table 4: Patron Eligibility

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 4.1 | Account suspended | Valid | patron.isAccountSuspended() == true | 3.0 | Suspended account, checkout denied |
| EP 4.2 | Fines >= $10 | Valid | patron.getFineBalance() >= 10.0 | 4.1 | Too much in fines, checkout denied |
| EP 4.3 | Overdue >= 3 | Valid | patron.getOverdueCount() >= 3 | 4.0 | Too many overdue, checkout denied |
| EP 4.4 | Patron fully eligible | Valid | Not suspended AND fines < $10 AND overdue < 3 | 0.0 - 1.1 | Passes patron eligibility, proceeds to book/limit checks |

### EP Table 5: Checkout Type (New vs Renewal)

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 5.1 | New checkout | Valid | patron.getCheckedOutBooks() does NOT contain book.isbn AND book is non-reference AND available | 0.0 - 1.1 | Book added to patron's list, copies decrease |
| EP 5.2 | Renewal | Valid | patron.getCheckedOutBooks() contains book.isbn AND book is non-reference AND available | 0.1 | Due date updated, copies unchanged |

### EP Table 6: Checkout Limits

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 6.1 | Below max limit | Valid | patron.getCheckoutCount() < maxLimit - 2 (patron eligible, book available) | 0.0 | Checkout succeeds without warning |
| EP 6.2 | Within 2 of max | Valid | patron.getCheckoutCount() >= maxLimit - 2 AND < maxLimit (patron eligible, book available) | 1.1 | Checkout succeeds WITH warning |
| EP 6.3 | At or over max limit | Valid | patron.getCheckoutCount() >= maxLimit | 3.2 | Patron at max, cannot checkout |

### EP Table 7: Success Cases with Warnings

| Partition ID | State | Valid/Invalid | Input Condition | Expected Return | Expected Behavior |
|--------------|-------|---------------|----------------|-----------------|------------------|
| EP 7.1 | 1-2 overdue books | Valid | patron.getOverdueCount() >= 1 AND <= 2, all other conditions pass | 1.0 | Checkout succeeds WITH warning about overdue |
| EP 7.2 | No warnings | Valid | Patron eligible, not near limit, few/no overdue | 0.0 | Clean success, no warnings |

---

## Part 2: Boundary Value Analysis (BVA)

Important BVA cases may overlap with EP. That is OK. You can reference all relevant EP/BVA coverage in Part 3.

### Example BVA Table: Overdue Count (Threshold: 3)

| Test ID | Boundary | Input Value | Expected Return | Rationale |
|---------|----------|-------------|-----------------|-----------|
| BVA 1.1 | Below | overdueCount = 0 | Success (depends on other setup) | Below warning threshold |
| BVA 1.2 | Warning High | overdueCount = 2 | 1.0 | Just below reject threshold |
| BVA 1.3 | At | overdueCount = 3 | 4.0 | At rejection boundary |
| BVA 1.4 | Above | overdueCount = 4 | 4.0 | Above rejection boundary |

---

### BVA Table 1: Fine Amount Threshold ($10.00)

| Test ID | Boundary | Input Value | Expected Return | Rationale |
|---------|----------|-------------|-----------------|-----------|
| BVA 1.1 | Below | fineBalance = $0.00 | 0.0 (or warnings) | No fine issues |
| BVA 1.2 | Below | fineBalance = $9.99 | 0.0 (or warnings) | Just below threshold |
| BVA 1.3 | At | fineBalance = $10.00 | 4.1 | At rejection boundary |
| BVA 1.4 | Above | fineBalance = $10.01 | 4.1 | Above rejection boundary |
| BVA 1.5 | Above | fineBalance = $25.00 | 4.1 | Well above threshold |

### BVA Table 2: Overdue Count Threshold (3 books)

| Test ID | Boundary | Input Value | Expected Return | Rationale |
|---------|----------|-------------|-----------------|-----------|
| BVA 2.1 | Below | overdueCount = 0 | 0.0 (or warnings) | No overdue books |
| BVA 2.2 | Below | overdueCount = 1 | 1.0 warning (if checkout succeeds) | 1-2 overdue triggers 1.0 |
| BVA 2.3 | Below | overdueCount = 2 | 1.0 warning (if checkout succeeds) | Just below reject threshold |
| BVA 2.4 | At | overdueCount = 3 | 4.0 | At rejection boundary |
| BVA 2.5 | Above | overdueCount = 4 | 4.0 | Above rejection boundary |

### BVA Table 3: Checkout Limit (varies by patron type)

| Test ID | Boundary | Patron Type | Input Value | Expected Return | Rationale |
|---------|----------|----------|-------------|-----------------|-----------|
| BVA 3.1 | Below | STUDENT | checkoutCount = 8 | 0.0 | Below limit (10), no warning |
| BVA 3.2 | Near | STUDENT | checkoutCount = 8 (after checkout = 9) | 0.0 | Within 2 of max (10), no warning yet |
| BVA 3.3 | Near | STUDENT | checkoutCount = 9 (after checkout = 10) | 1.1 warning | Within 2 of max (10), triggers warning |
| BVA 3.4 | At | STUDENT | checkoutCount = 10 | 3.2 | At max limit (10), cannot checkout |
| BVA 3.5 | Near | FACULTY | checkoutCount = 18 (after = 19) | 1.1 warning | Within 2 of max (20) |
| BVA 3.6 | At | FACULTY | checkoutCount = 20 | 3.2 | At max limit (20) |
| BVA 3.7 | At | CHILD | checkoutCount = 3 | 3.2 | At max limit (3) |

---

## Part 3: Test Cases Designed

List at least **20** test cases you designed based on your EP/BVA analysis.

Each test case should include:
- EP/BVA coverage
- specific inputs / setup
- expected return code
- expected **observable state changes** (if any)

> Do not test console output.

### Test Case Table

At least some of your tests should verify observable state changes, not just return values.

**Checkout0-3 Columns:** Mark each implementation as Pass (✓) or Fail (✗) for this test case. This helps you track which implementations have bugs and will be useful for Part 4 analysis.

| Test ID | EP/BVA Coverage | Input Description | Expected Return | Expected State Changes | Notes |
|---------|--------|-------------------|-----------------|------------------------|-------|
| T1 | EP 1.1 | book = null, patron = eligible | 2.1 | No changes | Null book rejection |
| T2 | EP 2.1 | book = unavailable (0 copies), patron = eligible | 2.0 | No changes | All copies checked out |
| T3 | EP 2.2, EP 5.1 | book = available, patron = eligible, new checkout, below limit | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | Successful checkout |
| T4 | EP 3.1 | patron = null, book = available | 3.1 | No changes | Null patron rejection |
| T5 | EP 4.1 | patron = suspended, book = available | 3.0 | No changes | Account suspended |
| T6 | BVA 1.3 | patron fines = $10.00, book = available | 4.1 | No changes | Fine amount at threshold |
| T7 | BVA 1.4 | patron fines = $10.01, book = available | 4.1 | No changes | Fine amount above threshold |
| T8 | BVA 2.4 | patron overdue = 3, book = available | 4.0 | No changes | Overdue count at threshold |
| T9 | BVA 2.3 | patron overdue = 2, eligible, book = available | 1.0 | book.availableCopies-1; patron.checkedOutBooks+1 | Success with overdue warning |
| T10 | EP 1.2 | book = reference-only, patron = eligible | 5.0 | No changes | Reference books cannot checkout |
| T11 | EP 5.2, BVA 2.1 | patron already has book (renewal), eligible, no overdue | 0.1 | book.availableCopies unchanged; due date updated | Renewal success |
| T12 | EP 6.1, BVA 3.1 | student patron, checkoutCount = 8, book = available | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | Below limit, no warning |
| T13 | EP 6.2, BVA 3.3 | student patron, checkoutCount = 9, book = available | 1.1 | book.availableCopies-1; patron.checkedOutBooks+1 | Within 2 of limit, warning 1.1 |
| T14 | EP 6.3, BVA 3.4 | student patron, checkoutCount = 10 (at max), book = available | 3.2 | No changes | At maximum checkout limit |
| T15 | EP 4.2, EP 5.1 | patron account active, no fines, no overdue, book = available | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | Fully eligible checkout |
| T16 | EP 4.3 | patron fines = $9.99, overdue = 0, book = available | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | Just below fine threshold |
| T17 | BVA 3.5 | faculty patron, checkoutCount = 18, book = available | 1.1 | book.availableCopies-1; patron.checkedOutBooks+1 | Within 2 of FACULTY limit (20) |
| T18 | BVA 3.7 | child patron, checkoutCount = 2, book = available | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | Child patron, room to checkout |
| T19 | EP 5.1, BVA 1.2 | patron fines = $9.99, overdue = 0, renewal of existing book | 0.1 | book.availableCopies unchanged; due date updated | Renewal with low fines |
| T20 | BVA 2.2 | patron overdue = 1, book = available, eligible otherwise | 1.0 | book.availableCopies-1; patron.checkedOutBooks+1 | 1-2 overdue warning (1.0) |
| T21 | EP 4.5, BVA 2.5 | patron overdue = 4, book = available | 4.0 | No changes | Overdue count above threshold |
| T22 | EP 5.1, EP 6.1 | staff patron, checkoutCount = 13 (below limit), book = available | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | STAFF patron, successful checkout |
| T23 | BVA 3.6 | faculty patron, checkoutCount = 20 (at max) | 3.2 | No changes | FACULTY at maximum (20) |
| T24 | EP 4.4, BVA 1.1 | patron fines = $0, all other conditions pass, book = available | 0.0 | book.availableCopies-1; patron.checkedOutBooks+1 | No fines, eligible checkout |

---

## Part 4: Bug Analysis

### Easter Eggs Found
List any easter egg messages you observed:
- "Testing can show the presence of bugs,"
- "but never their absence"
- "- Dijkstra"
- "The happy path matters too."
- "Renew, reuse, recycle... books."
- "Reference books are meant to be consulted, not carried home."

### Implementation Results

| Implementation | Bugs Found (count) |
|----------------|--------------------|
| Checkout0      |         4          |
| Checkout1      |         4          |
| Checkout2      |         4          |
| Checkout3      |         4          |
(for the sake of keeping it managable I grouped a few like bugs together)

### Bugs Discovered
List distinct bugs you identified for each implementation. Each bug must cite at least one test case that revealed it.

**Checkout0:**
- Bug 1: Successful new checkouts do not decrease available copies. — Revealed by: T3, T15, T16, T20, T24
- Bug 2: Reference books return the wrong error code. 2.0 instead of 5.0. — Revealed by: T10
- Bug 3: Faculty patrons at the maximum checkout limit can still checkout. — Revealed by: T23
- Bug 4: Child/student below-limit cases incorrectly return warning code 1.1 instead of success 0.0. — Revealed by: T12, T18

**Checkout1:**
- Bug 1: Successful checkout doesn't add checked out book to the patrons list. — Revealed by: T3, T15, T16, T24
- Bug 2: Reference books incorrectly change available copies. — Revealed by: T10
- Bug 3: Faculty at the maximum checkout limit are allowed instead of rejected. — Revealed by: T23
- Bug 4: Student/child below-limit cases incorrectly return warning code 1.1 instead of success 0.0. — Revealed by: T12, T18

**Checkout2:**
- Bug 1: Unavailable books incorrectly return success code 0.0 instead of error 2.0. — Revealed by: T1
- Bug 2: Renewals return 0.0 instead of 0.1. — Revealed by: T11, T19
- Bug 3: Near-limit warning cases return the wrong code. — Revealed by: T13
- Bug 4: Faculty at the maximum checkout limit are allowed to checkout. — Revealed by: T23

**Checkout3:**
- Bug 1: Renewals decrease available copies. — Revealed by: T11, T19
- Bug 2: Reference books incorrectly change available copies. — Revealed by: T10
- Bug 3: Overdue warning cases return 0.0 instead of 1.0. — Revealed by: T9
- Bug 4: Faculty patrons at the maximum checkout limit are allowed instead of rejected. — Revealed by: T23

### Comparative Analysis
Compare the four implementations:
- Which bugs are most critical (cause the worst failures)? Bugs that don't correctly change the system are the most critical they lead to other failures .
- Which implementation would you use if you had to choose? Checkout1
- Why? Justify your choice considering bug severity and frequency. It won't lead to system failures, the errors are more contained. The worst case is the faculty checkout more books than allowed, which isn't that big of a deal.

---

## Part 5: Reflection

**Which testing technique was most effective for finding bugs?** Creating your own blackbox testing around expected system behaviour.

**What was the most challenging aspect of this assignment?** Wrapping my head around equivalence partitions was challenging. I went over many iterations trying to figure out a way that made sense. Creating the tests would have been the most challenging but the example tests were very helpful.

**How did you decide on your EP and BVA?** I used the attributes of the Patron and Book classes to find groups of equivalence partitions. The requirements had values for most of those, I took those values and went way below, just below, on, just above, and way above for the most part.

**Describe one test where checking only the return value would NOT have been sufficient to detect a bug.**
Most of them, the basic successful checkokut would not be sufficient. It could return as successful but it might not change the available books, or update the list of checkout books of the patron.

