import java.io.Serializable;

/**
 * Egy könyvet leíró osztály.
 */
public class Book implements Serializable {

    /**
     * A könyv szerzője.
     */
    private String author;

    /**
     * A könyv címe.
     */
    private String title;

    /**
     * A könyv kiadási éve.
     */
    private int yearOfPublication;

    /**
     * A könyv kategóriája.
     */
    private BookCategory category;

    /**
     * A könyv nyelve.
     */
    private String language;

    /**
     * A könyv kölcsönözhetősége.
     */
    private boolean isBorrowable;

    /**
     * Az a {@code Member}, aki kikölcsönözte a könyvet.
     */
    private Member borrowedBy;


    /**
     * Konstruktor
     *
     * @param author            A könyv szerzője
     * @param title             A könyv címe
     * @param yearOfPublication A könyv kiadásának éve
     * @param category          A könyv kategóriája
     * @param language          A könyv nyelve
     * @param isBorrowable      A könyv kölcsönözhetőségének állapota
     */
    public Book(String author, String title, int yearOfPublication, BookCategory category, String language, boolean isBorrowable) {
        this.author = author;
        this.title = title;
        this.yearOfPublication = yearOfPublication;
        this.category = category;
        this.language = language;
        this.isBorrowable = isBorrowable;
    }

    /**
     * @return A könyv címe
     */
    public String getTitle() {
        return title;
    }

    /**
     * Beállítja a könyv címét.
     *
     * @param title A könyv új címe
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return A könyv szerzője
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Beállítja a könyv szerzőjét.
     *
     * @param author A könyv új szerzője
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return A könyv nyelve
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Beállítja a könyv nyelvét.
     *
     * @param language A könyv nyelve
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return A könyv kiadásának éve
     */
    public int getYearOfPublication() {
        return yearOfPublication;
    }

    /**
     * Beállítja a könyv kiadásának évét.
     *
     * @param yearOfPublication A könyv kiadási éve
     */
    public void setYearOfPublication(int yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    /**
     * @return A könyv kategóriája.
     */
    public BookCategory getCategory() {
        return category;
    }

    /**
     * Beállítja a könyv kategóriáját.
     *
     * @param category A könyv kategóriája
     */
    public void setCategory(BookCategory category) {
        this.category = category;
    }

    /**
     * @return A könyv kölcsönözhető-e
     */
    public boolean isBorrowable() {
        return isBorrowable;
    }

    /**
     * Beállítja, hogy egy könyv kölcsönözhető-e.
     *
     * @param borrowable A könyv új kölcsönözhetőségi állapota
     */
    public void setBorrowable(boolean borrowable) {
        isBorrowable = borrowable;
    }

    /**
     * @return A {@code Member}, aki kikölcsönözte a könyvet
     */
    public Member getBorrowedBy() {
        return borrowedBy;
    }

    /**
     * Beállítja, hogy ki kölcsönözte ki a könyvet.
     *
     * @param borrowedBy Az a {@code Member}, aki kikölcsönözte a könyvet
     */
    public void setBorrowedBy(Member borrowedBy) {
        if (this.isBorrowable)
            this.borrowedBy = borrowedBy;
    }
}
