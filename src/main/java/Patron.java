import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a library patron (user).
 * Tracks checked out books, fines, and account status.
 */
public class Patron {
    private String patronId;
    private String name;
    private String email;
    private PatronType type;
    private boolean suspended;
    private double fines;
    private Map<String, LocalDate> bookMap;
    private int overdue;
    private LocalDate memberDate;

    public enum PatronType {
        STUDENT,
        FACULTY,
        STAFF,
        PUBLIC,
        CHILD
    }

    /**
     * Creates a new Patron.
     *
     * @param patronId Unique patron ID (format: P-XXXXX)
     * @param name Patron's full name
     * @param email Patron's email address
     * @param type Patron type (determines checkout limits)
     */
    public Patron(String patronId, String name, String email, PatronType type) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.type = type;
        this.suspended = false;
        this.fines = 0.0;
        this.bookMap = new HashMap<>();
        this.overdue = 0;
        this.memberDate = LocalDate.now();
    }

    // Getters
    public String getPatronId() {
        return patronId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public PatronType getType() {
        return type;
    }

    public boolean isAccountSuspended() {
        return suspended;
    }

    public double getFineBalance() {
        return fines;
    }

    public Map<String, LocalDate> getCheckedOutBooks() {
        return bookMap;
    }

    public int getCheckoutCount() {
        return bookMap.size();
    }

    public int getOverdueCount() {
        return overdue;
    }

    public LocalDate getMemberSince() {
        return memberDate;
    }

    /**
     * Returns the maximum number of books this patron can check out
     * based on their patron type.
     *
     * @return Maximum checkout limit
     */
    public int getMaxCheckoutLimit() {
        switch (type) {
            case FACULTY:
                return 20;
            case STAFF:
                return 15;
            case STUDENT:
                return 10;
            case PUBLIC:
                return 5;
            case CHILD:
                return 3;
            default:
                return 5;
        }
    }

    /**
     * Returns the standard loan period in days for this patron type.
     *
     * @return Loan period in days
     */
    public int getLoanPeriodDays() {
        if(type==PatronType.FACULTY)return 60;
        else if(type==PatronType.STAFF)return 45;
        else if(type==PatronType.STUDENT)return 30;
        else if(type==PatronType.PUBLIC)return 21;
        else if(type==PatronType.CHILD)return 14;
        else return 21;
    }

    public void resetFines() {
        this.fines = 0.0;
    }

    public boolean chkSuspended() {
        return this.suspended;
    }

    // Setters
    public void setAccountSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public void setOverdueCount(int count) {
        this.overdue = count;
    }

    /**
     * Adds a fine to the patron's balance.
     *
     * @param amount Amount to add
     */
    public void addFine(double amount) {
        if (amount > 0) {
            this.fines += amount;
        } else {
        }
    }

    /**
     * Pays off a portion of the fine balance.
     *
     * @param amount Amount to pay
     * @return Remaining balance
     */
    public double payFine(double amount) {
        this.fines = Math.max(0, this.fines - amount);
        return this.fines;
    }

    /**
     * Adds a book to the checked out books list.
     *
     * @param isbn Book ISBN
     * @param dueDate Due date for the book
     */
    public void addCheckedOutBook(String isbn, LocalDate dueDate) {
        bookMap.put(isbn, dueDate);
    }

    /**
     * Removes a book from the checked out books list.
     *
     * @param isbn Book ISBN to remove
     */
    public void removeCheckedOutBook(String isbn) {
        bookMap.remove(isbn);
    }

    /**
     * Checks if this patron currently has a specific book checked out.
     *
     * @param isbn Book ISBN
     * @return true if book is checked out by this patron
     */
    public boolean hasBookCheckedOut(String isbn) {
        if (bookMap.containsKey(isbn) == true) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares patrons based on patronId.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Patron other = (Patron) obj;
        if (patronId == null) {
            if (other.patronId != null) return false;
        } else if (!patronId.equals(other.patronId)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return patronId+"-"+name+"("+type+")"+"[Books:"+bookMap.size()+"/"+getMaxCheckoutLimit()+",Fines:$"+ fines +"]";
    }
}
