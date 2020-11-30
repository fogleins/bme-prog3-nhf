package library.impl;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

/**
 * A keresés szövegmezőt reprezentáló osztály.
 */
public class BookSearchBar extends JTextField {
    private String placeholderText;
    private List<Book> books;

    /**
     * Konstruktor, inicializálja a szükséges adattagokat.
     *
     * @param books A lista, amiben a keresési mezőt használva keresünk
     */
    BookSearchBar(List<Book> books) {
        this.books = books;
        this.placeholderText = "Keresés " + books.size() + " könyv között";
        this.setText(placeholderText);

        this.addFocusListener(new BookSearchBarFocusListener());
    }

    /**
     * Beállítja a listát és frissíti a placeholder szöveget.
     *
     * @param books A lista, amin a keresési mezőt a továbbiakban használni szeretnénk
     */
    public void setBooks(List<Book> books) {
        this.books = books;
        update();
    }

    /**
     * Frissíti a placeholder szöveget.
     */
    public void update() {
        this.placeholderText = "Keresés " + books.size() + " könyv között";
        this.setText(placeholderText);
    }

    /**
     *
     * @return A jelenlegi placeholder szöveg.
     */
    public String getCurrentPlaceholderText() {
        return placeholderText;
    }

    /**
     * A keresési mező fókuszba kerülését vagy a fókusz elvesztését kezelő osztály. Ha a mező üres, és kikerül a fókuszból,
     * akkor beállítjuk a placeholder szöveget, ha a mező fókuszba kerül, akkor pedig eltávolítjuk azt. A felhasználó által
     * bevitt szöveget nem írja felül.
     */
    class BookSearchBarFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            if (BookSearchBar.this.getText().equals(placeholderText))
                BookSearchBar.this.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (BookSearchBar.this.getText().equals(placeholderText) || BookSearchBar.this.getText().equals(""))
                BookSearchBar.this.update();
        }
    }
}
