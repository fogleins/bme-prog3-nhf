import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AuthorData extends AbstractTableModel {

    List<Author> authors = new ArrayList<>();

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return this.authors.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Author author = authors.get(rowIndex);
        switch(columnIndex) {
            case 0: return author.getName();
            case 1: return author.getBirthyear();
            default: return author.getNativeLanguage();
        }
    }

    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Név";
            case 1: return "Születési év";
            default: return "Anyanyelv";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 1: return Integer.class;
            default: return String.class;
        }
    }
}
