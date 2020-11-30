package library;

import library.impl.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class LibraryTest {
    Library library;

    @Before
    public void setUp() {
        library = new Library();
        library.initTransientVariables("library.libdat");
    }

    @Test
    public void checkDefaultPath() {
        Assert.assertEquals("library.libdat", library.getSerializationPath());
    }

    @Test
    public void addBook() {
        // addBook() metódust teszteli helyes könyvadatokkal
        assertTrue(library.addBook("J. K. Rowling", "Harry Potter és a Titkok Kamrája", "2000", BookCategory.YOUTH, "magyar", true));
        Assert.assertEquals(1, library.getBooks().size());
    }

    @Test
    public void addBookInvalidArguments() {
        // addBook() metódust teszteli hibás adatokkal
        // érvénytelen szerző
        Assert.assertFalse(library.addBook("", "Harry Potter és a Titkok Kamrája", "2000", BookCategory.YOUTH, "magyar", true));
        Assert.assertEquals(0, library.getBooks().size());
        // érvénytelen cím
        assertFalse(library.addBook("J. K. Rowling", "", "2000", BookCategory.YOUTH, "magyar", true));
        Assert.assertEquals(0, library.getBooks().size());
        // érvénytelen kiadási év
        assertFalse(library.addBook("J. K. Rowling", "Harry Potter és a Titkok Kamrája", "abc", BookCategory.YOUTH, "magyar", true));
        Assert.assertEquals(0, library.getBooks().size());
    }

    @Test
    public void removeBook() {
        // könyv eltávolítását teszteli
        library.addBook("J. K. Rowling", "Harry Potter és a Titkok Kamrája", "2000", BookCategory.YOUTH, "magyar", true);
        Assert.assertEquals(1, library.getBooks().size());
        // az ellenőrzés miatt az addBook() metódus paraméterként veszi át a könyv adatait, majd felépít egy Book objektumot,
        // ezért a tesztben el kell kérni a tárolt könyv referenciáját. (A "sima" működés során a táblázat tudja, hogy melyik
        // könyv van kiválasztva, és annak a referenciájával eltávolítható a könyv, nem kell lekérdezni a listától.)
        library.remove(library.getBooks().get(0));
        Assert.assertEquals(0, library.getBooks().size());
    }

    @Test
    public void addMember() {
        Assert.assertTrue(library.addMember("Kiss Ferenc", "2000-01-01", "06301234567"));
        Assert.assertEquals(1, library.getMembers().size());
    }

    @Test
    public void editMember() {
        library.addMember("Kiss Ferenc", "2000-01-01", "06301234567");
        Member member = library.getMembers().get(0);
        library.editMember(member, "Kis Ferenc", "2000-02-02", "06201234567");
        Assert.assertTrue(member.getName().equals("Kis Ferenc") &&
                member.getDateOfBirth().isEqual(LocalDate.of(2000, 2, 2)) &&
                member.getPhone().equals("06201234567"));
    }

    @Test
    public void editMemberInvalidArguments() {
        library.addMember("Kiss Ferenc", "2000-01-01", "06301234567");
        Member member = library.getMembers().get(0);
        library.editMember(member, "Kis Ferenc", "2000-02-02", "telefonszám");
        Assert.assertFalse(member.getName().equals("Kis Ferenc") &&
                member.getDateOfBirth().isEqual(LocalDate.of(2000, 2, 2)) &&
                member.getPhone().equals("telefonszám"));
    }

    @Test
    public void removeMember() {
        library.addMember("Kiss Ferenc", "2000-01-01", "06301234567");
        library.remove(library.getMembers().get(0));
        Assert.assertEquals(0, library.getMembers().size());
    }

    @Test
    public void borrow() {
        Member member = new Member("Kovács István", LocalDate.of(1947, 10, 2), "06701234567");
        this.library.getMembers().add(member);
        Assert.assertEquals(0, member.getBorrowedBooks().size()); // biztosra megyünk, hogy a tag valóban 0 kölcsönzött könyvvel rendelkezik a létrehozás után
        Book book = new Book("J. K. Rowling", "Harry Potter és a Bölcsek Köve", 1999, BookCategory.YOUTH, "magyar", true);
        this.library.getBookData().addBook(book);
        this.library.getBookData().setValueAt(member, 0, 6); // beállítjuk a tagot kölcsönzőként
        Assert.assertEquals(member.getName(), this.library.getBookData().getValueAt(0, 6)); // ellenőrizzük, hogy sikerült-e beállítani kölcsönzőként
        Assert.assertEquals(1, member.getBorrowedBooks().size()); // megnézzük, hogy a kölcsönzéskor felvettük-e a könyvet a tag által kölcsönzött könyvek közé
        Assert.assertSame(book.getBorrowedBy(), member); // megnézzük, hogy a könyvnél is be lett-e állítva, hogy kikölcsönözték
    }
}