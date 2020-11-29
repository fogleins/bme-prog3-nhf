import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

/**
 * A keresés szövegmezőt reprezentáló osztály.
 */
public class BookSearchBar extends JTextField {
    private String placeholderText;
    private final List<Book> books;

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

    public void update() {
        this.placeholderText = "Keresés " + books.size() + " könyv között";
        this.setText(placeholderText);
    }

    public String getCurrentPlaceholderText() {
        return placeholderText;
    }

    class BookSearchBarFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            if (BookSearchBar.this.getText().equals(placeholderText))
                BookSearchBar.this.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (BookSearchBar.this.getText().equals(placeholderText) || BookSearchBar.this.getText().equals(""))
                update(); // TODO
        }
    }
}
