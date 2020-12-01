package library.impl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;

/**
 * A könyveket megjelenítő táblázat modellje.
 */
public class BookData extends AbstractTableModel {

    /**
     * A szerializációhoz használt egyedi osztályazonosító.
     */
    private static final long serialVersionUID = 2640480992917852652L;

    /**
     * A megjelenítendő könyvek listája.
     */
    List<Book> books;

    /**
     * Konstruktor
     */
    public BookData() {
        this.books = new ArrayList<>();
    }

    /**
     * Konstruktor
     *
     * @param books A megjelenítendő könyvek listája
     */
    public BookData(List<Book> books) {
        this.books = books;
    }

    /**
     * Visszaadja a táblázat oszlopainak számát.
     *
     * @return A táblázat oszlopainak száma
     */
    @Override
    public int getColumnCount() {
        return 7;
    }

    /**
     * Visszaadja a táblázat sorainak a számát.
     *
     * @return A táblázat sorainak száma
     */
    @Override
    public int getRowCount() {
        return this.books.size();
    }

    /**
     * Visszaadja a táblázat egy adott cellájában lévő értéket.
     *
     * @param rowIndex    A sor indexe
     * @param columnIndex Az oszlop indexe
     * @return A paraméterként kapott sor- és oszlopindex által megadott cella értéke
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return book.getAuthor();
            case 1:
                return book.getTitle();
            case 2:
                return book.getYearOfPublication();
            case 3:
                return book.getCategory().getLocalizedName();
            case 4:
                return book.getLanguage();
            case 5:
                return book.isBorrowable();
            default:
                Member borrowedBy = book.getBorrowedBy();
                if (borrowedBy != null)
                    return borrowedBy.getName();
                return null;
        }
    }

    /**
     * Visszaadja a táblázat egy oszlopának a fejlécében megjelenítendő szöveget.
     *
     * @param column Az oszlop, amelynek a nevét szeretnénk megtudni
     * @return Az adott oszlop neve
     */
    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Szerző";
            case 1:
                return "Cím";
            case 2:
                return "Kiadás éve";
            case 3:
                return "Kategória";
            case 4:
                return "Nyelv";
            case 5:
                return "Kölcsönözhető?";
            default:
                return "Kikölcsönözte";
        }
    }

    /**
     * Visszaadja, hogy a táblázat egy adott oszlopa milyen típusú adatokat jelenít meg.
     *
     * @param columnIndex Az oszlop, amelynek a típusát szeretnénk lekérdezni
     * @return A paraméterként kapott oszlopban megjelenített adatok típusa
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 2:
                return Integer.class;
            case 5:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    /**
     * Visszaadja, hogy egy adott cella szerkeszthető-e.
     *
     * @param rowIndex    A cella sora
     * @param columnIndex A cella oszlopa
     * @return Igaz, ha a cella szerkeszthető
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * Beállítja a paraméterül kapott cella értékét.
     *
     * @param aValue      Az érték, amit be szeretnénk állítani a cellában
     * @param rowIndex    A cella sora
     * @param columnIndex A cella oszlopa
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Book selectedBook = books.get(rowIndex);
        switch (columnIndex) {
            case 0:
                String author = (String) aValue;
                if (!author.equals(""))
                    selectedBook.setAuthor(author);
                break;
            case 1:
                String title = (String) aValue;
                if (!title.equals(""))
                    selectedBook.setTitle(title);
                break;
            case 2:
                int year = (Integer) aValue;
                if (year != 0)
                    selectedBook.setYearOfPublication(year);
                break;
            case 3:
                selectedBook.setCategory((BookCategory.valueOf((String) aValue, "HU")));
                break;
            case 4:
                String lang = (String) aValue;
                if (!lang.equals(""))
                    selectedBook.setLanguage(lang.toLowerCase());
                break;
            case 5:
                BorrowManager.borrowableChanged(selectedBook, aValue);
                fireTableDataChanged();
                break;
            case 6:
                if (selectedBook.isBorrowable()) {
                    BorrowManager.borrowedByChanged(selectedBook, aValue);
                    fireTableDataChanged();
                }
                break;
        }
    }

    /**
     * Felvesz egy könyvet a programba.
     *
     * @param book A felveendő könyv
     */
    public void addBook(Book book) {
        books.add(book);
        fireTableDataChanged();
    }

    /**
     * Eltávolít egy könyvet a programból.
     *
     * @param book Az eltávolítandó könyv
     */
    public void removeBook(Book book) {
        books.remove(book);
        fireTableDataChanged();
    }

    /**
     * A könyvek táblázatában csak a kölcsönzött könyveket mutatja.
     *
     * @return A {@code RowSorter}, amit használva csak a kölcsönzött könyvek lesznek láthatóak
     */
    public RowSorter<BookData> showBorrowedOnly() {
        RowFilter<BookData, Integer> bookFilter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends BookData, ? extends Integer> entry) {
                Book book = books.get(entry.getIdentifier());
                return book.getBorrowedBy() != null;
            }
        };
        TableRowSorter<BookData> sorter = new TableRowSorter<>(this);
        sorter.setRowFilter(bookFilter);
        return sorter;
    }
}
