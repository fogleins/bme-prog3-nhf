package library.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Könyvtári tagot megvalósító osztály.
 */
public class Member implements Serializable {

    /**
     * A szerializációhoz használt egyedi osztályazonosító.
     */
    private static final long serialVersionUID = 5796688182502854713L;

    /**
     * A tag neve.
     */
    private String name;

    /**
     * A tag születési dátuma.
     */
    private LocalDate dateOfBirth;

    /**
     * A tag telefonszáma.
     */
    private String phone;

    /**
     * A tagság kezdete.
     */
    private final LocalDate memberSince;

    /**
     * A tag által kikölcsönzött könyvek listája.
     */
    private final List<Book> borrowedBooks;


    /**
     * Konstruktor
     *
     * @param name        A tag neve
     * @param dateOfBirth A tag születési ideje
     * @param phone       A tag telefonszáma
     */
    public Member(String name, LocalDate dateOfBirth, String phone) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.memberSince = LocalDate.now();
        this.borrowedBooks = new ArrayList<>();
    }

    /**
     * @return A tag neve
     */
    public String getName() {
        return name;
    }

    /**
     * Beállítja a tag nevét.
     *
     * @param name A tag új neve
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return A tag születési dátuma.
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Beállítja a tag születési dátumát.
     *
     * @param dateOfBirth A tag új születési dátuma
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return A tag telefonszáma
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Beállítja a tag telefonszámát.
     *
     * @param phone A tag új telefonszáma
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return A tagság kezdete
     */
    public LocalDate getMemberSince() {
        return memberSince;
    }

    /**
     * @return A tag által kikölcsönzött könyvek listája
     */
    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    /**
     * Visszaadja a tag adatait tartalmazó {@code String}-et.
     *
     * @return A tag neve, születési ideje, telefonszáma és a kölcsönzött könyvek száma, {@code String}-ként
     */
    @Override
    public String toString() {
        return name + " (" + dateOfBirth + ", " + phone + "): " + borrowedBooks.size() + " könyv";
    }
}
