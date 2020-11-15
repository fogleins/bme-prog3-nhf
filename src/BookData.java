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
        Book newBook = new Book(author, title, year, category, language, borrowable);
        books.add(newBook);
        fireTableDataChanged();
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 5;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1)
            books.get(rowIndex).setTitle((String) aValue);
        else if (columnIndex == 2)
            books.get(rowIndex).setYear((Integer) aValue);
        else if (columnIndex == 3)
            books.get(rowIndex).setCategory((BookCategory.valueOf((String) aValue, "HU")));
        else if (columnIndex == 5)
            books.get(rowIndex).setBorrowable((Boolean) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
