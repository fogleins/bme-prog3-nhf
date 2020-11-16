import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AuthorData extends AbstractTableModel {

    List<Author> authors = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return this.authors.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Author author = authors.get(rowIndex);
        switch(columnIndex) {
            case 0: return author.getName();
            case 1: return author.getBirthyear();
            default: return author.getNativeLanguage().getLocalizedName();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Név";
            case 1: return "Születési év";
            default: return "Anyanyelv";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1)
            return Integer.class;
        return String.class;
    }

    public void addAuthor(String name, int birthyear, NativeLanguage language) {
        authors.add(new Author(name, birthyear, language));
        fireTableDataChanged();
    }
}
