import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;

public class ApplicationFrame extends JFrame {

    private BookData data;

    /**
     * Létrehozza és beállítja az ablak komponenseit
     */
    private void init() {
        this.setLayout(new BorderLayout());
        JTable bookTable = new JTable(data);
//        bookTable.setFillsViewportHeight(true);
        bookTable.setAutoCreateRowSorter(true);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        this.add(bookScrollPane, BorderLayout.SOUTH); // TODO

        // menüsor
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("Fájl");
        JMenuItem open = new JMenuItem("Megnyitás");
        JMenuItem save = new JMenuItem("Mentés");
        JMenuItem exit = new JMenuItem("Kilépés");
        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exit);

        JMenu edit = new JMenu("Szerkesztés");

        JMenu library = new JMenu("Könyvtár");
        JMenuItem addNew = new JMenuItem("Új könyv hozzáadása");
        JMenuItem remove = new JMenuItem("Könyv eltávolítása");
        library.add(addNew);
        library.add(remove);

        // JMenuItem-ek hozzáadása a menüsorhoz
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(library);

        this.setJMenuBar(menuBar);
        this.setLocationRelativeTo(null); // az ablak képernyő közepén való megjelenítéséhez

        // kategória JComboBox-szal való megadsához
        TableColumn categoryColumn = bookTable.getColumnModel().getColumn(3);
        JComboBox<String> categoryComboBox = new JComboBox<>();
        for (BookCategory category : BookCategory.values())
            categoryComboBox.addItem(category.getLocalizedName());
        categoryColumn.setCellEditor(new DefaultCellEditor(categoryComboBox));
        System.out.println(bookTable.getRowHeight());
        bookTable.setRowHeight(20);
    }

    //    @SuppressWarnings("unchecked")
    public ApplicationFrame() {
        super("Könyvtárkezelő");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));

        try {
            data = new BookData();
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("books.libdat"));
            data.books = (List<Book>) inputStream.readObject();
            inputStream.close();
        } catch (FileNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(null, "A fájl nem található: "
                    + notFoundException.getMessage(), "A fájl nem található", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("books.libdat"));
                    outputStream.writeObject(data.books);
                    outputStream.close();
                } catch (Exception ex) {
                    // ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Hiba az adatok mentése során: A könyvek mentése sikertelen ("
                                    + ex.getMessage() + ')', "Hiba az adatok mentése során", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        init();
    }

    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        }
//        catch (Exception e) {
//            System.out.println("err");
//        }
        ApplicationFrame frame = new ApplicationFrame();
        frame.setVisible(true);
    }
}
