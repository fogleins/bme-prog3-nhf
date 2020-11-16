import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookData extends AbstractTableModel {
    List<Book> books = new ArrayList<>();

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
        switch(columnIndex) {
            case 0: return book.getAuthor().getName();
            case 1: return book.getTitle();
            case 2: return book.getYear();
            case 3: return book.getCategory().getLocalizedName();
            case 4: return book.getLanguage();
            case 5: return book.isBorrowable();
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
            case 0: return "Szerző";
            case 1: return "Cím";
            case 2: return "Kiadás éve";
            case 3: return "Kategória";
            case 4: return "Nyelv";
            case 5: return "Kölcsönözhető?";
            default: return "Kikölcsönözte";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 2: return Integer.class;
            case 3: return BookCategory.class;
            case 5: return Boolean.class;
            default: return String.class;
        }
    }

    public void addBook(Author author, String title, int year, BookCategory category, String language, boolean borrowable) {
        books.add(new Book(author, title, year, category, language, borrowable));
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Book selectedBook = books.get(rowIndex);
        if (columnIndex == 1)
            selectedBook.setTitle((String) aValue);
        else if (columnIndex == 2)
            selectedBook.setYear((Integer) aValue);
        else if (columnIndex == 3)
            selectedBook.setCategory((BookCategory.valueOf((String) aValue, "HU")));
        else if (columnIndex == 5) {
            if (selectedBook.getBorrowedBy() != null) {
                int chosenOption = JOptionPane.showConfirmDialog(null, "A választott könyvet " +
                        "kikölcsönözték. Szeretné nem kölcsönözhetővé állítani? A hozzárendelt kölcsönző eltűnik.",
                        "A választott könyvet kikölcsönözték", JOptionPane.YES_NO_OPTION);
                if (chosenOption == JOptionPane.YES_OPTION)
                    selectedBook.setBorrowedBy(null);
                else return;
            }
            selectedBook.setBorrowable((Boolean) aValue);
            fireTableDataChanged();
        }
        else if (columnIndex == 6) {
            if (selectedBook.isBorrowable())
                selectedBook.setBorrowedBy((Member) aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
