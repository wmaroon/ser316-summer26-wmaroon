import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Sample White-Box tests for the Checkout system.
 * This class demonstrates how to write white-box tests using:
 * - Control Flow Graph (CFG) analysis
 * - Statement coverage
 * - Branch coverage
 * - Path coverage
 *
 * White-box testing focuses on testing the IMPLEMENTATION by
 * examining the code structure and ensuring all paths are tested.
 */
public class CheckoutWhiteBoxSample {

    private Checkout checkout;

    @BeforeEach
    public void setUp() {
        checkout = new Checkout();
    }

    @Test
    @DisplayName("WB Test: countBooksByType - null type branch")
    public void testCountBooksByType_NullType() {
        // Branch: type == null → TRUE
        int result = checkout.countBooksByType(null, false);
        assertEquals(0, result, "Should return 0 for null type");
    }

    @Test
    @DisplayName("WB Test: ISBN null")
    public void testISBN_Null() {
        assertFalse(checkout.isValidISBN(null));
    }

    @Test
    @DisplayName("WB Test: ISBN contains letters")
    public void testISBN_ContainsLetters() {
        assertFalse(checkout.isValidISBN("ABC123"));
    }

    @Test
    @DisplayName("WB Test: ISBN valid 10 digits")
    public void testISBN_Valid10() {
        assertTrue(checkout.isValidISBN("0123456789"));
    }

    @Test
    @DisplayName("WB Test: ISBN wrong length")
    public void testISBN_WrongLength() {
        assertFalse(checkout.isValidISBN("12345"));
    }

    @Test
    public void testPatronType_NullString() {
        assertFalse(checkout.isPatronType(null, Patron.PatronType.STUDENT));
    }

    @Test
    public void testPatronType_NullType() {
        assertFalse(checkout.isPatronType("STUDENT", null));
    }

    @Test
    public void testPatronType_Match() {
        assertTrue(
            checkout.isPatronType(
                Patron.PatronType.STUDENT.toString(),
                Patron.PatronType.STUDENT
            )
        );
    }

    @Test
    public void testPatronType_NoMatch() {
        assertFalse(
            checkout.isPatronType(
                Patron.PatronType.FACULTY.toString(),
                Patron.PatronType.STUDENT
            )
        );
    }
    
    @Test
    public void testCalculateFineZeroDays() {
        assertEquals(0.0,
                checkout.calculateFine(0, Book.BookType.FICTION));
    }

    @Test
    public void testCalculateFineFiveDays() {
        assertEquals(1.25,
                checkout.calculateFine(5, Book.BookType.FICTION));
    }

    @Test
    public void testCalculateFineTenDays() {
        assertEquals(3.25,
                checkout.calculateFine(10, Book.BookType.NONFICTION));
    }

    @Test
    public void testCalculateFineReferenceBook() {
        assertEquals(2.50,
                checkout.calculateFine(5, Book.BookType.REFERENCE));
    }

    @Test
    public void testCalculateFineCap() {
        assertEquals(25.0,
                checkout.calculateFine(100, Book.BookType.FICTION));
    }

    @Test
    public void testReturnBookInvalidPatron() {
        assertEquals(-1.0,
                checkout.returnBook("123", null));
    }

    @Test
    public void testReturnBookNotOverdue() {

        Patron p = new Patron(
                "P1",
                "Bob",
                "bob@test.com",
                Patron.PatronType.STUDENT);

        Book b = new Book(
                "1234567890",
                "Book",
                "Author",
                Book.BookType.FICTION,
                1);

        checkout.registerPatron(p);
        checkout.addBook(b);

        checkout.checkoutBook(b, p);

        double fine = checkout.returnBook("1234567890", p);

        assertEquals(0.0, fine);
    }

    @Test
    public void testReturnBookNullPatron() {
        assertEquals(-1.0,
            checkout.returnBook("1234567890", null));
    }

}
