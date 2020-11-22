import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {
    private String name;
    private LocalDate dateOfBirth;
    private String phone;
    private final LocalDate memberSince;
    private List<Book> borrowedBooks;

    public Member(String name, LocalDate dateOfBirth, String phone, LocalDate memberSince) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.memberSince = memberSince;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    @Override
    public String toString() {
        return name + " (" + dateOfBirth + ", " + phone + "): "+ borrowedBooks.size() + " könyv";
    }
}
