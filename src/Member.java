import java.io.Serializable;
import java.time.LocalDate;

public class Member implements Serializable {
    private String name;
    private LocalDate dateOfBirth;
    private String phone;

    public Member(String name, LocalDate dateOfBirth, String phone) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
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

    @Override
    public String toString() {
        return name + " (" + dateOfBirth + ", " + phone + ")";
    }
}
