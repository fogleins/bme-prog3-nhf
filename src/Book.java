import java.io.Serializable;

public class Book implements Serializable {

    private String author;
    private String title;
    private int year;
    private BookCategory category;
    private String language;
    private boolean isBorrowable;
    private Member borrowedBy;

    public Book(String author, String title, int year, BookCategory category, String language, boolean isBorrowable) {
        this.author = author;
        this.title = title;
        this.year = year;
        this.category = category;
        this.language = language;
        this.isBorrowable = isBorrowable;
//        this.borrowedBy = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BookCategory getCategory() {
        return category;
    }

    public void setCategory(BookCategory category) {
        this.category = category;
    }

    public void setCategory(String category) {
        this.category = BookCategory.valueOf(category);
    }

    public boolean isBorrowable() {
        return isBorrowable;
    }

    public void setBorrowable(boolean borrowable) {
        isBorrowable = borrowable;
    }


    public Member getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(Member borrowedBy)/* throws BookNotBorrowableException */{ // TODO
        if (this.isBorrowable)
            this.borrowedBy = borrowedBy;
//        else
//            throw new BookNotBorrowableException("This book is not borrowable.");
    }
}
