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

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public int getRowCount() {
        return this.books.size();
    }

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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Book selectedBook = books.get(rowIndex);
        if (columnIndex == 0) {
            String author = (String) aValue;
            if (!author.equals(""))
                selectedBook.setAuthor(author);
        } else if (columnIndex == 1) {
            String title = (String) aValue;
            if (!title.equals(""))
                selectedBook.setTitle(title);
        } else if (columnIndex == 2) {
            int year = (Integer) aValue;
            if (year != 0)
                selectedBook.setYearOfPublication(year);
        } else if (columnIndex == 3)
            selectedBook.setCategory((BookCategory.valueOf((String) aValue, "HU")));
        else if (columnIndex == 4) {
            String lang = (String) aValue;
            if (!lang.equals(""))
                selectedBook.setLanguage(lang.toLowerCase());
        } else if (columnIndex == 5) {
            BorrowManager.borrowableChanged(selectedBook, aValue);
            fireTableDataChanged();
        } else if (columnIndex == 6) {
            if (selectedBook.isBorrowable()) {
                BorrowManager.borrowedByChanged(selectedBook, aValue);
                fireTableDataChanged();
            }
        }
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
