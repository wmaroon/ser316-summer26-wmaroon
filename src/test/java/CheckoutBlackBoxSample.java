import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sample Black-Box tests for the Checkout system.
 * This class demonstrates how to write black-box tests using:
 * - Equivalence Partitioning (EP)
 * - Boundary Value Analysis (BVA)
 * - Parametrized tests across multiple implementations
 *
 * Black-box testing focuses on testing the SPECIFICATION WITHOUT
 * looking at the implementation.
 *
 * The parameterized structure allows testing all Checkout implementations
 * with the same tests to identify which implementations have bugs.
 */
public class CheckoutBlackBoxSample {

    private Checkout checkout;

    /**
     * Provides the list of Checkout classes to test.
     * Each test will run against ALL implementations.
     
    @SuppressWarnings("unchecked")
    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
        return (Stream<Class<? extends Checkout>>) Stream.of(
                Checkout0.class,
                Checkout1.class,
                Checkout2.class,
                Checkout3.class
        );
    }
    */
  
    static Stream<Class<? extends Checkout>> checkoutClassProvider() {
        return Stream.of(Checkout.class);
    }


    /**
     * Helper method to create Checkout instance from class using reflection.
     */
    private Checkout createCheckout(Class<? extends Checkout> clazz) throws Exception {
        Constructor<? extends Checkout> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    /**
     * SAMPLE TEST 1: Tests successful checkout of an available book
     * This tests the valid equivalence partition - all conditions met.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T2: Successful checkout - available book, eligible patron")
    public void testBookAvailable(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create available book and eligible patron
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book); // adding the book to the library
        checkout.registerPatron(patron); // adding a patrol to the system

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 0.0 for success
        assertEquals(0.0, result, 0.01,
                "Expected successful checkout (0.0) for " + checkoutClass.getSimpleName());

        // Verify: Book should now be unavailable
        assertFalse(book.isAvailable(),
                "Book should be unavailable after checkout for " + checkoutClass.getSimpleName());

        // Verify: Patron should have the book in their checked-out list
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should have book in checked-out list for " + checkoutClass.getSimpleName());

        // Verify: Checkout count increased
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * SAMPLE TEST 2: Tests checkout with unavailable book
     * This tests an invalid equivalence partition.
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T1: Unavailable book returns error code 2.0")
    public void testUnavailableBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup: Create unavailable book
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 5);
        book.setAvailableCopies(0);  // We are pretending it has been checked out by others and is not available anymore

        Patron patron = new Patron("P001", "Test Patron", "test@example.com",
                Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute checkout
        double result = checkout.checkoutBook(book, patron);

        // Verify: Should return 2.0 for unavailable book
        assertEquals(2.0, result, 0.01,
                "Expected error code 2.0 for unavailable book for " + checkoutClass.getSimpleName());

        // Verify: Patron should NOT have the book
        assertFalse(patron.hasBookCheckedOut(book.getIsbn()),
                "Patron should NOT have book in list for " + checkoutClass.getSimpleName());
    }

    // ========== TEST STUBS - IMPLEMENT BASED ON MARKDOWN DESIGN ==========

    /**
     * T1: Null book rejection
     * EP 1.1 - book = null, patron = eligible → 2.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T1: Book is null - error 2.1")
    public void testNullBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        
        // Setup
        Book book = null;
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(2.1, result, 0.001,
                "Expected error code 2.1 for null book for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for null book for " + checkoutClass.getSimpleName());
    }

    /**
     * T3: Successful checkout - available book, eligible patron, below limit
     * EP 2.2, EP 5.1 - book available, patron eligible, new checkout, below limit → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T3: Successful checkout - available book, eligible patron")
    public void testSuccessfulNewCheckout(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        
        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(0.0, result, 0.001,
                "Expected success code 0.0 for successful checkout for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(), 
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * T4: Null patron rejection
     * EP 3.1 - patron = null, book available → 3.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T4: Patron is null - error 3.1")
    public void testNullPatron(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        
        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = null;
        
        checkout.addBook(book);
        
        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(3.1, result, 0.001,
                "Expected error code 3.1 for null patron for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for null patron for " + checkoutClass.getSimpleName());
    }

    /**
     * T5: Account suspended
     * EP 4.1 - patron suspended, book available → 3.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T5: Account suspended - error 3.0")
    public void testAccountSuspended(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        
        // Setup:
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.setAccountSuspended(true);

        checkout.addBook(book);
        checkout.registerPatron(patron);
        // Execute:
        double result = checkout.checkoutBook(book, patron);
        
        
        // Verify:
        assertEquals(3.0, result, 0.001,
                "Expected error code 3.0 for suspended account for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for suspended account for " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for suspended account for " + checkoutClass.getSimpleName());
    }

    /**
     * T6: Fines at exact threshold
     * BVA 1.3 - patron fines = $10.00, book available → 4.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T6: Fines at $10.00 threshold - error 4.1")
    public void testFinesAtThreshold(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);
        // Setup:

        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.addFine(10.00);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(4.1, result, 0.001,
                "Expected error code 4.1 for fines at $10.00 threshold for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for fines at threshold for " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for fines at threshold for " + checkoutClass.getSimpleName());
    }

    /**
     * T7: Fines above threshold
     * BVA 1.4 - patron fines = $10.01, book available → 4.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T7: Fines above $10.00 - error 4.1")
    public void testFinesAboveThreshold(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.addFine(10.01);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(4.1, result, 0.001,
                "Expected error code 4.1 for fines above $10.00 threshold for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for fines above threshold for " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for fines above threshold for " + checkoutClass.getSimpleName());
    }

    /**
     * T8: Overdue count at threshold
     * BVA 2.4 - patron overdue = 3, book available → 4.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T8: Overdue count at 3 (threshold) - error 4.0")
    public void testOverdueAtThreshold(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.setOverdueCount(3);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(4.0, result, 0.001,
                "Expected error code 4.0 for overdue count at threshold for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for overdue at threshold for " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for overdue at threshold for " + checkoutClass.getSimpleName());
    }

    /**
     * T9: Success with overdue warning
     * BVA 2.3 - patron overdue = 2, eligible, book available → 1.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T9: Success with warning - 2 overdue books (1.0)")
    public void testSuccessWithOverdueWarning(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.setOverdueCount(2);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.0, result, 0.001,
                "Expected success code 1.0 for patron with overdue warning for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * T10: Reference book rejection
     * EP 1.2 - book reference-only, patron eligible → 5.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T10: Reference book - error 5.0")
    public void testReferenceBook(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Reference Book",
                "Test Author", Book.BookType.REFERENCE, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
                double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(5.0, result, 0.001,
                "Expected error code 5.0 for reference book for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for reference book for " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for reference book for " + checkoutClass.getSimpleName());
    }

    /**
     * T11: Renewal success
     * EP 5.2, BVA 2.1 - patron already has book (renewal), eligible → 0.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T11: Renewal - patron renews existing book (0.1)")
    public void testRenewalSuccess(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.addCheckedOutBook("978-0-123456-78-9", LocalDate.now());

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(0.1, result, 0.001,
                "Expected success code 0.1 for successful renewal for " + checkoutClass.getSimpleName());
        
        // Verify state changes - book copies unchanged, due date updated
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for renewal for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should still be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should remain 1 for renewal for " + checkoutClass.getSimpleName());
    }

    /**
     * T12: Student below checkout limit
     * EP 6.1, BVA 3.1 - student checkoutCount = 8, book available → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T12: Student below limit (8/10) - success (0.0)")
    public void testStudentBelowLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        book.setAvailableCopies(1); // Ensure book is available

        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        for (int i = 0; i < 8; i++) {
            patron.addCheckedOutBook("978-0-123456-78-" + (i+1), LocalDate.now());
        }

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.1, result, 0.001,
                "Expected success code 1.1 for student below checkout limit for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(9, patron.getCheckoutCount(),
                "Patron checkout count should be 9 after checkout for " + checkoutClass.getSimpleName());
    }

    /**
     * T13: Student within 2 of limit (warning)
     * EP 6.2, BVA 3.3 - student checkoutCount = 9, book available → 1.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T13: Student within 2 of limit (9/10) - warning (1.1)")
    public void testStudentWithinLimitWarning(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        book.setAvailableCopies(1); // Ensure book is available

        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        
        for (int i = 100; i < 109; i++) {
            patron.addCheckedOutBook("978-0-123456-" + (i), LocalDate.now());
        }
        

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.1, result, 0.001,
                "Expected warning code 1.1 for student within 2 of limit for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(10, patron.getCheckoutCount(),
                "Patron checkout count should be 10 after checkout for " + checkoutClass.getSimpleName());
    }

    /**
     * T14: Student at maximum limit
     * EP 6.3, BVA 3.4 - student checkoutCount = 10 (at max), book available → 3.2
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T14: Student at max limit (10/10) - error 3.2")
    public void testStudentAtMaxLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        for (int i = 0; i < 10; i++) {
        patron.addCheckedOutBook(
                String.format("978-0-123456-%03d", i),
                LocalDate.now().plusDays(3)
        );
        }

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(3.2, result, 0.001,
                "Expected error code 3.2 for student at maximum limit for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for student at max limit for " + checkoutClass.getSimpleName());
        assertEquals(10, patron.getCheckoutCount(),
                "Patron checkout count should remain 10 for student at max limit for " + checkoutClass.getSimpleName());
    }

    /**
     * T15: Fully eligible checkout
     * EP 4.2, EP 5.1 - no fines, no overdue, active account → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T15: Fully eligible - no fines, no overdue (0.0)")
    public void testFullyEligibleCheckout(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(0.0, result, 0.001,
                "Expected success code 0.0 for fully eligible checkout for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * T16: Fines just below threshold
     * EP 4.3 - patron fines = $9.99, overdue = 0 → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T16: Fines just below $10 ($9.99) - success (0.0)")
    public void testFinesBelowThreshold(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.addFine(9.99);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(0.0, result, 0.001,
                "Expected success code 0.0 for fines just below threshold for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * T17: Faculty within 2 of limit
     * BVA 3.5 - faculty checkoutCount = 18, book available → 1.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T17: Faculty within 2 of limit (18/20) - warning (1.1)")
    public void testFacultyWithinLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.FACULTY);
        for (int i = 0; i < 18; i++) {
        patron.addCheckedOutBook(
                String.format("978-0-123456-%03d", i),
                LocalDate.now().plusDays(3)
        );
        }

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.1, result, 0.001,
                "Expected warning code 1.1 for faculty within 2 of limit for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(19, patron.getCheckoutCount(),
                "Patron checkout count should be 19 after checkout for " + checkoutClass.getSimpleName());
    }

    /**
     * T18: Child patron below limit
     * BVA 3.7 - child checkoutCount = 2, book available → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T18: Child patron below limit (2/3) - success (0.0)")
    public void testChildPatronCheckout(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.CHILD);
        for (int i = 0; i < 2; i++) {
        patron.addCheckedOutBook(
                String.format("978-0-123456-%03d", i),
                LocalDate.now().plusDays(3)
        );
        }

        checkout.addBook(book);
        checkout.registerPatron(patron);
        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.1, result, 0.001,
                "Expected success code 1.1 for child patron below limit for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(3, patron.getCheckoutCount(),
                "Patron checkout count should be 3 after checkout for " + checkoutClass.getSimpleName());
    }

    /**
     * T19: Renewal with low fines
     * EP 5.1, BVA 1.2 - patron fines $9.99, renewal of existing book → 0.1
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T19: Renewal with low fines (0.1)")
    public void testRenewalWithLowFines(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.addFine(9.99);
        patron.addCheckedOutBook("978-0-123456-78-9", LocalDate.now());
        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(0.1, result, 0.001,
                "Expected success code 0.1 for renewal with low fines for " + checkoutClass.getSimpleName());
        
        // Verify state changes - book copies unchanged, due date updated
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for renewal for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should still be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should remain 1 for renewal for " + checkoutClass.getSimpleName());
    }

    /**
     * T20: Success with 1 overdue book (warning)
     * BVA 2.2 - patron overdue = 1, eligible otherwise → 1.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T20: Success with warning - 1 overdue book (1.0)")
    public void testSuccessWithOneOverdue(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.setOverdueCount(1);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.0, result, 0.001,
                "Expected warning code 1.0 for patron with 1 overdue book for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

    /**
     * T21: Overdue above threshold
     * EP 4.5, BVA 2.5 - patron overdue = 4, book available → 4.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T21: Overdue count above 3 (4) - error 4.0")
    public void testOverdueAboveThreshold(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.setOverdueCount(4);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(4.0, result, 0.001,
                "Expected error code 4.0 for patron with 4 overdue books for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(1, book.getAvailableCopies(),
                "Book available copies should not change for overdue above threshold for " + checkoutClass.getSimpleName());
        assertEquals(0, patron.getCheckoutCount(),
                "Patron checkout count should not change for overdue above threshold for " + checkoutClass.getSimpleName());
    }

    /**
     * T22: Staff patron below limit
     * EP 5.1, EP 6.1 - staff checkoutCount = 13, book available → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T22: Staff below limit (13/15) - success (1.1)")
    public void testStaffBelowLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STAFF);
        for (int i = 0; i < 13; i++) {
        patron.addCheckedOutBook(
                String.format("978-0-123456-%03d", i),
                LocalDate.now().plusDays(3)
        );
        }

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(1.1, result, 0.001,
                "Expected success code 1.1 for staff patron below limit for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(14, patron.getCheckoutCount(),
                "Patron checkout count should be 14 after checkout for " + checkoutClass.getSimpleName());
    }

    /**
     * T23: Faculty at maximum limit
     * BVA 3.6 - faculty checkoutCount = 20 (at max) → 3.2
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T23: Faculty at max limit (20/20) - error 3.2")
    public void testFacultyAtMaxLimit(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 20);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.FACULTY);
        for (int i = 0; i < 20; i++) {
        patron.addCheckedOutBook(
                String.format("978-0-123456-%03d", i),
                LocalDate.now().plusDays(3)
        );
        }

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(3.2, result, 0.001,
                "Expected error code 3.2 for faculty at maximum limit for " + checkoutClass.getSimpleName());
        
        // Verify no state change
        assertEquals(20, book.getAvailableCopies(),
                "Book available copies should not change for faculty at max limit for " + checkoutClass.getSimpleName());
        assertEquals(20, patron.getCheckoutCount(),
                "Patron checkout count should remain 20 for faculty at max limit for " + checkoutClass.getSimpleName());
    }

    /**
     * T24: No fines checkout
     * EP 4.4, BVA 1.1 - patron fines = $0, all conditions pass → 0.0
     */
    @ParameterizedTest
    @MethodSource("checkoutClassProvider")
    @DisplayName("T24: No fines, all conditions pass (0.0)")
    public void testNoFinesCheckout(Class<? extends Checkout> checkoutClass) throws Exception {
        checkout = createCheckout(checkoutClass);

        // Setup
        Book book = new Book("978-0-123456-78-9", "Test Book",
                "Test Author", Book.BookType.FICTION, 1);
        Patron patron = new Patron("P001", "Test Patron", "test@example.com", Patron.PatronType.STUDENT);
        patron.addFine(0.0);

        checkout.addBook(book);
        checkout.registerPatron(patron);

        // Execute
        double result = checkout.checkoutBook(book, patron);

        // Verify
        assertEquals(0.0, result, 0.001,
                "Expected success code 0.0 for patron with no fines for " + checkoutClass.getSimpleName());
        
        // Verify state changes
        assertEquals(0, book.getAvailableCopies(),
                "Book available copies should decrease by 1 for " + checkoutClass.getSimpleName());
        assertTrue(patron.hasBookCheckedOut(book.getIsbn()),
                "Book should be in patron's checked-out list for " + checkoutClass.getSimpleName());
        assertEquals(1, patron.getCheckoutCount(),
                "Patron checkout count should be 1 for " + checkoutClass.getSimpleName());
    }

}
