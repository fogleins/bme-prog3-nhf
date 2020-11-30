package library;

import library.impl.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BookDataTest {
    BookData bookData;

    @Before
    public void init() {
        this.bookData = new BookData();
        this.bookData.addBook(new Book("J. K. Rowling", "Harry Potter", 2000, BookCategory.YOUTH, "magyar", false));
    }

    // a könyveket a tagokkal ellentétben lehet közvetlenül a táblázatban szerkeszteni, itt azt teszteljük, hogy valóban
    // nem engedi-e, hogy érvénytelen adatokat adjunk meg
    @Test
    public void setInvalidStringValue() {
        // az üres stringet nem menti, helyette megmarad a korábbi érték
        bookData.setValueAt("", 0, 1);
        Assert.assertNotEquals("", bookData.getValueAt(0, 1));
        Assert.assertEquals("Harry Potter", bookData.getValueAt(0, 1));
    }

    // int oszlopban nem lehet stringet megadni, Exception-t dob
    @Test(expected = ClassCastException.class)
    public void setInvalidIntValue() {
        bookData.setValueAt("", 0, 2);
    }

    // helyes adatokat beírva a módosítások mentésre kerülnek
    @Test
    public void setValue() {
        bookData.setValueAt("Arany János", 0, 0);
        bookData.setValueAt("Toldi", 0, 1);
        bookData.setValueAt(true, 0, 5);
        Assert.assertEquals("Arany János", bookData.getValueAt(0, 0));
        Assert.assertEquals("Toldi", bookData.getValueAt(0, 1));
        Assert.assertTrue((Boolean) bookData.getValueAt(0, 5));
    }
}
