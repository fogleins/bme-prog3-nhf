import javax.swing.table.AbstractTableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AuthorData extends AbstractTableModel {

    List<Author> authors;

    public AuthorData() {
        this.authors = new ArrayList<>();
    }

    public AuthorData(List<Author> authors) {
        this.authors = new ArrayList<>(authors);
    }

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
        switch (columnIndex) {
            case 0:
                return author.getName();
            case 1:
                return author.getBirthyear();
            default:
                return author.getNativeLanguage().getLocalizedName();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Név";
            case 1:
                return "Születési év";
            default:
                return "Anyanyelv";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1)
            return Integer.class;
        return String.class;
    }

    private boolean listContains(Author author) {
        for (Author a : authors) {
            if (a.getName().equals(author.getName()) && a.getBirthyear() == author.getBirthyear()
                    && a.getNativeLanguage() == author.getNativeLanguage())
                return true;
        }
        return false;
    }

    public void addAuthor(Author author) {
        if (!listContains(author)) {
            authors.add(author);
            fireTableDataChanged();
        }
    }

    public void addAuthor(String name, int birthyear, NativeLanguage language) {
        Author author = new Author(name, birthyear, language);
        if (!listContains(author)) {
            authors.add(author);
            fireTableDataChanged();
        }
    }

    public void removeAuthor(Author author) { // TODO: nem létező jelzése?
        authors.remove(author);
        fireTableDataChanged();
    }
}
