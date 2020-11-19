import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

public class ApplicationFrame extends JFrame {

    private Library library;

    private JTable bookTable;
    private JTable authorTable;
    private JTable memberTable;
    private JPanel northPanel;
    private JButton addBookButton;
    private JButton removeBookButton;


    private void readDataFromFile() {
        this.library = Library.readDataFromFile(this.library);
        this.bookTable.setModel(this.library.bookData);
        this.authorTable.setModel(this.library.authorData);
        this.memberTable.setModel(this.library.memberData);
        initCellEditors();
    }

    private void showOpenFileDialog() {
        JFileChooser chooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("libdat adatfájlok", "libdat");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.library.serializationPath = chooser.getSelectedFile().getPath();
            readDataFromFile();
        }
    }

    /**
     * Beállítja az ablak tulajdonságait
     */
    private void initFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1280, 720));
        this.setPreferredSize(new Dimension(1280, 720));
        this.setLocationRelativeTo(null); // az ablak képernyő közepén való megjelenítéséhez
        this.setLayout(new BorderLayout());

        // adatok mentése kilépéskor
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                library.saveData();
            }
        });
    }

    /**
     * Létrehozza, beállítja és az ablakhoz adja a menüsort
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Fájl");
        JMenuItem open = new JMenuItem("Megnyitás");
        open.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
        open.addActionListener(new ActionListener() {  // megnyitásra kattintva megnyitja a tallózási ablakot
            @Override
            public void actionPerformed(ActionEvent e) {
                showOpenFileDialog();
            }
        });
        JMenuItem exit = new JMenuItem("Kilépés");
        exit.setAccelerator(KeyStroke.getKeyStroke('W', CTRL_DOWN_MASK)); // TODO: action listener
        JMenuItem save = new JMenuItem("Mentés");
        save.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
        save.addActionListener(new ActionListener() { // mentésre kattintva elmenti az adatokat
            @Override
            public void actionPerformed(ActionEvent e) {
                library.saveData();
            }
        });

        JMenu editMenu = new JMenu("Szerkesztés");

        JMenu libraryMenu = new JMenu("Könyvtár");
        JMenuItem addNew = new JMenuItem("Új könyv hozzáadása");
        addNew.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
        addNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                library.addBook();
            }
        });
        JMenuItem remove = new JMenuItem("Könyv eltávolítása");
        remove.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookTable.getSelectedRow() >= 0)
                    library.removeBook(library.bookData.books.get(bookTable.convertRowIndexToModel(bookTable.getSelectedRow())));
            }
        });

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(exit);

        libraryMenu.add(addNew);
        libraryMenu.add(remove);

        // JMenuItem-ek hozzáadása a menüsorhoz
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(libraryMenu);

        this.setJMenuBar(menuBar);
    }

    /**
     * Létrehozza és beállítja a könyvek megjelenítéséért felelős komponenseket
     */
    private void initBookTable() {
        this.bookTable.setIntercellSpacing(new Dimension(10, 5)); // cellák ne folyjanak össze
        this.bookTable.setAutoCreateRowSorter(true);
        JScrollPane bookScrollPane = new JScrollPane(this.bookTable);

        // Panelek
        JPanel booksPanel = new JPanel();
        booksPanel.setBorder(BorderFactory.createTitledBorder("Könyvek"));
        booksPanel.setLayout(new BorderLayout());

        // komponenspanel a táblázat fölé
        JPanel booksNorthPanel = new JPanel();
        BoxLayout northPanelLayout = new BoxLayout(booksNorthPanel, BoxLayout.X_AXIS);

        booksNorthPanel.setLayout(northPanelLayout);
        this.addBookButton = new JButton("Könyv hozzáadása...");
        this.addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                library.addBook();
            }
        });
        booksNorthPanel.add(this.addBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        this.removeBookButton = new JButton("Könyv eltávolítása");
        this.removeBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookTable.getSelectedRow() >= 0)
                    library.removeBook(library.bookData.books.get(bookTable.convertRowIndexToModel(bookTable.getSelectedRow())));
            }
        });
        booksNorthPanel.add(this.removeBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        JCheckBox showBorrowedBooksOnly = new JCheckBox("Csak a kölcsönzött könyvek mutatása");
        booksNorthPanel.add(showBorrowedBooksOnly);  // TODO: action listener a toggle-re

        // keresés komponensei
        JFormattedTextField searchBar = new JFormattedTextField("Keresés " /*+ library.bookData.books.size() +*/ + " könyv között");
        booksNorthPanel.add(searchBar);
        JCheckBox searchAuthorToo = new JCheckBox("Keresés a szerzők nevében is");
        JButton searchButton = new JButton("Keresés");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: keresés
            }
        });
        booksNorthPanel.add(searchAuthorToo);
        booksNorthPanel.add(searchButton);

        booksPanel.add(booksNorthPanel, BorderLayout.NORTH);
        booksPanel.add(bookScrollPane, BorderLayout.CENTER);
        this.add(booksPanel, BorderLayout.CENTER);

        bookTable.setRowHeight(20);
        // TODO: https://stackoverflow.com/questions/13192419/setting-a-tooltip-for-a-value-from-jcomboboxs-items-as-celleditor-in-jtable
    }

    /**
     * Létrehozza és beállítja a szerzők megjelenítéséért felelős komponenseket
     */
    private void initAuthorTable() {
        this.authorTable.setIntercellSpacing(new Dimension(10, 5)); // cellák ne folyjanak össze
        this.authorTable.setRowHeight(18);
        this.authorTable.setAutoCreateRowSorter(true);
        JScrollPane authorScrollPane = new JScrollPane(this.authorTable);
        // panel
        JPanel authorsPanel = new JPanel(new BorderLayout());
        authorsPanel.setBorder(BorderFactory.createTitledBorder("Szerzők"));
        JPanel authorsComponentPanel = new JPanel();
        authorsComponentPanel.setLayout(new BoxLayout(authorsComponentPanel, BoxLayout.X_AXIS));
        JButton addAuthor = new JButton("Szerző hozzáadása...");
        addAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                library.addAuthor();
            }
        });
        authorsComponentPanel.add(addAuthor);
        JButton removeAuthor = new JButton("Szerző eltávolítása");
        removeAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authorTable.getSelectedRow() >= 0)
                    library.removeAuthor(library.authorData.authors.get(
                            authorTable.convertRowIndexToModel(authorTable.getSelectedRow())));
            }
        });
        authorsComponentPanel.add(removeAuthor);

        authorsPanel.add(authorsComponentPanel, BorderLayout.NORTH);
        authorsPanel.add(authorScrollPane, BorderLayout.CENTER);
        this.northPanel.add(authorsPanel);
    }

    /**
     * Létrehozza és beállítja a könyvtári tagok megjelenítéséért felelős komponenseket
     */
    private void initMemberTable() {
        this.memberTable.setIntercellSpacing(new Dimension(10, 2)); // cellák ne folyjanak össze
        this.memberTable.setRowHeight(18);
        this.memberTable.setAutoCreateRowSorter(true);
        JScrollPane memberScrollPane = new JScrollPane(this.memberTable);
        this.northPanel.add(memberScrollPane);
        // panel
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Könyvtári tagok"));
        JPanel membersComponentPanel = new JPanel();
        membersComponentPanel.setLayout(new BoxLayout(membersComponentPanel, BoxLayout.X_AXIS));
        JButton addMember = new JButton("Tag hozzáadása...");
        addMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                library.addMember();
            }
        });
        membersComponentPanel.add(addMember);
        JButton removeMember = new JButton("Tag eltávolítása");
        removeMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (memberTable.getSelectedRow() >= 0)
                    library.removeMember(library.memberData.members.get(
                            memberTable.convertRowIndexToModel(memberTable.getSelectedRow())));
            }
        });
        membersComponentPanel.add(removeMember);
        membersPanel.add(membersComponentPanel, BorderLayout.NORTH);

        membersPanel.add(memberScrollPane, BorderLayout.CENTER);
        this.northPanel.add(membersPanel, BorderLayout.CENTER);
    }

    /**
     * Beállítja azokat a {@code CellEditor}-okat, amiknek JComboBox-szal lehet megadni az értékét
     */
    private void initCellEditors() {
        // book table
        // kölcsönző JComboBox-szal való megadsához
        TableColumn memberColumn = bookTable.getColumnModel().getColumn(6);
        JComboBox<Member> memberComboBox = this.library.memberData.getMembersComboBox();
        memberColumn.setCellEditor(new DefaultCellEditor(memberComboBox));

        // kategória JComboBox-szal való megadsához
        TableColumn categoryColumn = bookTable.getColumnModel().getColumn(3);
        JComboBox<String> categoryComboBox = new JComboBox<>();
        for (BookCategory category : BookCategory.values())
            categoryComboBox.addItem(category.getLocalizedName());
        categoryColumn.setCellEditor(new DefaultCellEditor(categoryComboBox));

        // author table
        TableColumn languageColumn = authorTable.getColumnModel().getColumn(2);
        JComboBox<String> languageComboBox = new JComboBox<>();
        for (NativeLanguage language : NativeLanguage.values())
            languageComboBox.addItem(language.getLocalizedName());
        languageColumn.setCellEditor(new DefaultCellEditor(languageComboBox));
    }

    /**
     * Konstruktor
     */
    public ApplicationFrame() {
        super("Könyvtárkezelő");
        this.library = new Library();
        this.library.serializationPath = "library.libdat";

        this.bookTable = new JTable();
        this.authorTable = new JTable();
        this.memberTable = new JTable();
        readDataFromFile();
        initFrame();
        initMenuBar();
        initBookTable();
        this.northPanel = new JPanel();
//        this.northPanel.setBorder(BorderFactory.createTitledBorder("Szerzők és Tagok"));
        this.northPanel.setLayout(new BoxLayout(this.northPanel, BoxLayout.X_AXIS));
        this.northPanel.setPreferredSize(new Dimension(1280, 250));
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
