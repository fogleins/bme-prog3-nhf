import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;

public class ApplicationFrame extends JFrame {

    private BookData bookData;
    private AuthorData authorData;
    private MemberData memberData;

    private JPanel northPanel;

    /**
     * Beállítja az ablak tulajdonságait
     */
    private void initFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));
        this.setLocationRelativeTo(null); // az ablak képernyő közepén való megjelenítéséhez
        this.setLayout(new BorderLayout());

        // adatok beolvasása a program indulásakor
        try {
            try {
                bookData = new BookData();
                ObjectInputStream bookInputStream = new ObjectInputStream(new FileInputStream("books.libdat"));
                bookData.books = (List<Book>) bookInputStream.readObject();
                bookInputStream.close();
            } catch (InvalidClassException e) {
                bookData = new BookData();
            }

            try {
                authorData = new AuthorData();
                ObjectInputStream authorInputStream = new ObjectInputStream(new FileInputStream("authors.libdat"));
                authorData.authors = (List<Author>) authorInputStream.readObject();
                authorInputStream.close();
            } catch (InvalidClassException e) {
                authorData = new AuthorData();
            }

            try {
                memberData = new MemberData();
                ObjectInputStream memberInputStream = new ObjectInputStream(new FileInputStream("members.libdat"));
                memberData.members = (List<Member>) memberInputStream.readObject();
                memberInputStream.close();
                memberData.initComboBoxList(); // a beolvasott adatokat hozzáadja a comboboxhoz TODO
            } catch (InvalidClassException e) {
                memberData = new MemberData();
            }
        } catch (FileNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(null, "A fájl nem található: "
                    + notFoundException.getMessage(), "A fájl nem található", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // adatok mentése kilépéskor
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ObjectOutputStream bookOutputStream = new ObjectOutputStream(new FileOutputStream("books.libdat"));
                    bookOutputStream.writeObject(bookData.books);
                    bookOutputStream.close();

                    ObjectOutputStream authorOutputStream = new ObjectOutputStream(new FileOutputStream("authors.libdat"));
                    authorOutputStream.writeObject(authorData.authors);
                    authorOutputStream.close();

                    ObjectOutputStream memberOutputStream = new ObjectOutputStream(new FileOutputStream("members.libdat"));
                    memberOutputStream.writeObject(memberData.members);
                    memberOutputStream.close();
                } catch (Exception ex) {
                    // ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Hiba az adatok mentése során: A könyvek mentése sikertelen ("
                            + ex.getMessage() + ')', "Hiba az adatok mentése során", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Létrehozza és az ablakhoz adja a menüsort
     */
    private void initMenuBar() {
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
    }

    /**
     * Létrehozza és beállítja a könyvek megjelenítéséért felelős komponenseket
     */
    private void initBookTable() {
        JTable bookTable = new JTable(bookData);
//        bookTable.setFillsViewportHeight(true);
        bookTable.setAutoCreateRowSorter(true);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        // Panel
        JPanel booksPanel = new JPanel();
        booksPanel.setBorder(BorderFactory.createTitledBorder("Könyvek"));
        booksPanel.setLayout(new BorderLayout());
        booksPanel.add(bookScrollPane, BorderLayout.CENTER);
        this.add(booksPanel, BorderLayout.CENTER);

        bookTable.setRowHeight(20);

        // kategória JComboBox-szal való megadsához
        TableColumn categoryColumn = bookTable.getColumnModel().getColumn(3);
        JComboBox<String> categoryComboBox = new JComboBox<>();
        for (BookCategory category : BookCategory.values())
            categoryComboBox.addItem(category.getLocalizedName());
        categoryColumn.setCellEditor(new DefaultCellEditor(categoryComboBox));

        // kölcsönző JComboBox-szal való megadsához
        TableColumn memberColumn = bookTable.getColumnModel().getColumn(6);
        JComboBox<Member> memberComboBox = memberData.membersComboBox;
        memberColumn.setCellEditor(new DefaultCellEditor(memberComboBox));
    }

    /** Létrehozza és beállítja a szerzők megjelenítéséért felelős komponenseket */
    private void initAuthorTable() {
        JTable authorTable = new JTable(authorData);
        authorTable.setAutoCreateRowSorter(true);
        JScrollPane authorScrollPane = new JScrollPane(authorTable);
        // panel
        JPanel authorsPanel = new JPanel();
        authorsPanel.setBorder(BorderFactory.createTitledBorder("Szerzők"));
        authorsPanel.setLayout(new BorderLayout());
        authorsPanel.add(authorScrollPane);
        this.northPanel.add(authorsPanel);
    }

    /** Létrehozza és beállítja a könyvtári tagok megjelenítéséért felelős komponenseket */
    private void initMemberTable() {
        JTable memberTable = new JTable(memberData);
        memberTable.setAutoCreateRowSorter(true);
        JScrollPane memberScrollPane = new JScrollPane(memberTable);
        this.northPanel.add(memberScrollPane);
        // panel
        JPanel membersPanel = new JPanel();
        membersPanel.setBorder(BorderFactory.createTitledBorder("Könyvtári tagok"));
        membersPanel.setLayout(new BorderLayout());
        membersPanel.add(memberScrollPane);
        this.northPanel.add(membersPanel, BorderLayout.CENTER);
    }

    //    @SuppressWarnings("unchecked")
    public ApplicationFrame() {
        super("Könyvtárkezelő");
        initFrame();
        initMenuBar();
        initBookTable();
        this.northPanel = new JPanel();
        this.northPanel.setBorder(BorderFactory.createTitledBorder("Szerzők és Tagok"));
        this.northPanel.setLayout(new BoxLayout(this.northPanel, BoxLayout.X_AXIS));
        this.add(northPanel, BorderLayout.NORTH);
        initAuthorTable();
        initMemberTable();
        this.pack();
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
