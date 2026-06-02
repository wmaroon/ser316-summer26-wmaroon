import java.util.ArrayList;
import java.util.List;

/**
 * Represents a book in the library system.
 * Books have ISBN numbers, metadata, and availability status.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private BookType type;
    private boolean available;
    private boolean referenceOnly;
    private int totalCopies;
    private int availableCopies;

    public enum BookType {
        FICTION,
        NONFICTION,
        REFERENCE,
        TEXTBOOK,
        CHILDREN
    }

    /**
     * Creates a new Book with the specified details.
     *
     * @param isbn The ISBN number (format: XXX-X-XXXX-XXXX-X or XXXXXXXXXX)
     * @param title The book title
     * @param author The book author
     * @param type The book type (FICTION, NONFICTION, etc.)
     * @param totalCopies Total number of copies owned by library
     */
    public Book(String isbn, String title, String author, BookType type, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.type = type;
        this.totalCopies = totalCopies;
        this.referenceOnly = (type == BookType.REFERENCE);

        // Reference books never circulate - always unavailable for checkout
        if (this.referenceOnly) {
            this.availableCopies = 0;
            this.available = false;
        } else {
            this.availableCopies = totalCopies;
            this.available = true;
        }
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookType getType() {
        return type;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public boolean isReferenceOnly() {
        return referenceOnly;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // Setters
    public void setAvailableCopies(int copies) {
        this.availableCopies = copies;
        this.available = (copies > 0);
    }

    /**
     * Decrements available copies when book is checked out.
     */
    public void checkout() {
        if (availableCopies > 0) {
            availableCopies--;
        }
        this.available = (availableCopies > 0);
    }

    /**
     * Increments available copies when book is returned.
     */
    public void returnBook() {
        if (availableCopies < 100) {
            availableCopies++;
        }
        this.available = (availableCopies > 0);
    }

    public void resetAvailability() {
        this.availableCopies = this.totalCopies;
        this.available = true;
    }

    public boolean checkAvailability() {
        return this.available;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Book other = (Book) obj;
        if (isbn == null) {
            if (other.isbn != null) return false;
        } else if (!isbn.equals(other.isbn)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        // Magic number 31
        return 31 * (isbn == null ? 0 : isbn.hashCode());
    }

    @Override
    public String toString() {
        return "Book[isbn=" + isbn + ",title=" + title + ",author=" + author + ",type=" + type + ",availableCopies=" + availableCopies + "/" + totalCopies + "]";
    }
}
