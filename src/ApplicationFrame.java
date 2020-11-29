import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

/**
 * A program ablaka.
 */
public class ApplicationFrame extends JFrame {

    /**
     * A könytár objektum, aminek az adatait megjeleníti a program.
     */
    private Library library;

    /**
     * A könyveket tartalmazó {@code JTable} objektum.
     */
    private final JTable bookTable;

    /**
     * A könyvtári tagokat tartalmazó {@code JTable} objektum.
     */
    private final JTable memberTable;

    /**
     * Az ablak északi panele, amin megjelenik a tagokat tartalmazó táblázat és az aktuális kölcsönzéseket nyilvántartó fa struktúra.
     */
    private final JPanel northPanel;

    /**
     * A képernyőt vízszintesen két részre osztó {@code JSplitPane} objektum, mely tartalmazza az északi panelt és a könyvek táblázatát.
     */
    private final JSplitPane horizontalSplitPane;

    /**
     * Az északi panel komponenseit függőlegesen kettéválasztó {@code JSplitPane} objektum.
     */
    private final JSplitPane verticalSplitPane;

    /**
     * A könyvek közti kereséshez használt szövegmező.
     */
    private BookSearchBar searchBar;

    /**
     * A kölcsönzések nyilvántartására használt {@code JTree} objektum.
     */
    private JTree borrowersTree;

    /**
     * Az ablak aktuális méretét nyilvántartó objektum. Az ablak átméretezésekor a komponensek képernyőn elfoglalt helyének
     * arányának megtartására használjuk.
     *
     * @see #initFrame()
     */
    private Dimension windowSize;

    /**
     * Renderer a könyveket tartalmazó táblázathoz; a tárolt könyvek elérhetősége alapján más-más betűstílussal jeleníti meg a sorokat.
     */
    private class BookTableCellRenderer implements TableCellRenderer {

        /**
         * A táblázat renderere.
         */
        private final TableCellRenderer renderer;

        /**
         * Konstruktor. Beállítja a táblázat rendererét.
         *
         * @param renderer A táblázat renderere
         */
        public BookTableCellRenderer(TableCellRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean borrowable = library.books.get(bookTable.convertRowIndexToModel(row)).isBorrowable();

            if (borrowable)
                if ((library.books.get(bookTable.convertRowIndexToModel(row)).getBorrowedBy()) != null)
                    component.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                else
                    component.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            else
                component.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            return component;
        }
    }

    /**
     * Visszaolvassa az adatokat egy korábban sorosított fájlból.
     */
    private void readDataFromFile() {
        this.library = Library.readDataFromFile(this.library);
        this.bookTable.setModel(this.library.bookData);
        this.memberTable.setModel(this.library.memberData);
        initCellEditors();
    }

    /**
     * Megjelenít egy tallózás ablakot, ahol ha a felhasználó megfelelő kiterjesztésű fájlt választ ki, akkor beolvassa belőle az adatokat.
     */
    private void showOpenFileDialog() {
        JFileChooser chooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("libdat adatfájlok", "libdat");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Megnyitás");
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.library.serializationPath = chooser.getSelectedFile().getPath();
            readDataFromFile();
        }
    }

    /**
     * Megjelenít egy "Mentés másként..." ablakot, amiben a felhasználó megadhatja, hogy hova szeretné menteni a fájlt.
     * Ezt követően a megadott helyre írja a fájlt.
     */
    private void showSaveAsDialog() {
        JFileChooser chooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("libdat adatfájlok", "libdat");
        chooser.setFileFilter(filter);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Mentés másként...");
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            library.saveDataAs(chooser.getSelectedFile().getPath());
        }
    }

    /**
     * Megjelenít egy könyv adatinak megadását lehetővé tevő ablakot. Ha valamelyik adat hibás, hibaüzenetet ad.
     */
    private void showAddBookDialog() {
        BookPanel bookPanel = new BookPanel();
        int chosenOption = JOptionPane.showConfirmDialog(null, bookPanel.getMainPanel(), "Új könyv felvétele", JOptionPane.OK_CANCEL_OPTION);
        if (chosenOption == JOptionPane.OK_OPTION)
            if (!library.addBook(bookPanel.getAuthor(), bookPanel.getTitle(), bookPanel.getYear(),
                    BookCategory.valueOf(bookPanel.getCategory(), "HU"), bookPanel.getLanguage(), bookPanel.isBorrowable()))
                JOptionPane.showMessageDialog(null, "Hibás adat került megadásra. Ellenőrizze " +
                        "a megadott adatokat.", "Hibás adat", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Megkérdezi a felhasználót, hogy biztosan törölni szeretné-e a könyvet; ha igen, törli.
     */
    private void showRemoveBookDialog() {
        if (bookTable.getSelectedRow() >= 0) {
            Book book = library.books.get(bookTable.convertRowIndexToModel(bookTable.getSelectedRow()));
            int chosenOption = JOptionPane.showConfirmDialog(null,
                    (book.getBorrowedBy() == null) ? "Biztosan törli a kiválasztott könyvet?" : "A kiválasztott könyvet kikölcsönözték. Biztosan törli?",
                    "Biztosan törli?", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION)
                library.remove(book);
        }
    }

    /**
     * Beállítja az ablak tulajdonságait
     */
    private void initFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.windowSize = new Dimension(1280, 720);
        this.setMinimumSize(windowSize);
        this.setPreferredSize(windowSize);
        this.setLocationRelativeTo(null); // az ablak képernyő közepén való megjelenítéséhez
        this.setLayout(new BorderLayout());

        // adatok mentése kilépéskor
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                library.saveData();
            }
        });

        // az ablak átméretezésekor megtartjuk a komponensek képernyőn elfoglalt helyének arányát
        // ha még nem inicializálódott a méret, beállítjuk az alapértelmezett méretet
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int horizontalDividerLocation = horizontalSplitPane.getDividerLocation();
                int verticalDividerLocation = verticalSplitPane.getDividerLocation();
                // alapértelmezett értékek
                if (windowSize.width == 1280 && windowSize.height == 720
                        && (horizontalDividerLocation == 464 || horizontalDividerLocation < 0)
                        && (verticalDividerLocation == 78 || verticalDividerLocation < 0)) {
                    horizontalSplitPane.setDividerLocation(0.75);
                    verticalSplitPane.setDividerLocation(0.35);
                    return;
                }
                int eHeight = e.getComponent().getHeight();
                int eWidth = e.getComponent().getWidth();
                if (horizontalDividerLocation > 0) {
                    double proportionalLocation = horizontalDividerLocation / (double) windowSize.width;
                    horizontalSplitPane.setDividerLocation((int) (eWidth * proportionalLocation));
                }
                if (verticalDividerLocation > 0) {
                    double proportionalLocation = verticalDividerLocation / (double) windowSize.height;
                    verticalSplitPane.setDividerLocation((int) (eHeight * proportionalLocation));
                }
                windowSize.setSize(eWidth, eHeight);
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
        // megnyitásra kattintva megnyitja a tallózási ablakot
        open.addActionListener(actionEvent -> showOpenFileDialog());
        JMenuItem save = new JMenuItem("Mentés");
        save.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
        // mentésre kattintva elmenti az adatokat
        save.addActionListener(actionEvent -> library.saveData());
        JMenuItem saveAs = new JMenuItem("Mentés másként...");
        saveAs.addActionListener(actionEvent -> showSaveAsDialog());
        JMenuItem exitWithoutSaving = new JMenuItem("Kilépés mentés nélkül");
        exitWithoutSaving.addActionListener(actionEvent -> System.exit(0));
        JMenuItem exit = new JMenuItem("Kilépés");
        exit.setAccelerator(KeyStroke.getKeyStroke('W', CTRL_DOWN_MASK));
        exit.addActionListener(actionEvent -> {
            // mint az EXIT_ON_CLOSE
            library.saveData();
            System.exit(0);
        });

        JMenu editMenu = new JMenu("Szerkesztés");

        JMenu libraryMenu = new JMenu("Könyvtár");
        JMenuItem addNew = new JMenuItem("Új könyv hozzáadása");
        addNew.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
        addNew.addActionListener(e -> showAddBookDialog());
        JMenuItem remove = new JMenuItem("Könyv eltávolítása");
        remove.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        remove.addActionListener(actionEvent -> showRemoveBookDialog());

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(exitWithoutSaving);
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
        // ha változik a könyvek valamilyen adata, az aktuális kölcsönzések fáját is frissítjük
        this.bookTable.addPropertyChangeListener(propertyChangeEvent -> reloadTree());
        JScrollPane bookScrollPane = new JScrollPane(this.bookTable);
        bookTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                searchBar.update();
                reloadTree();
            }
        });

        // Panelek
        JPanel booksPanel = new JPanel();
        booksPanel.setBorder(BorderFactory.createTitledBorder("Könyvek"));
        booksPanel.setLayout(new BorderLayout());

        // komponenspanel a táblázat fölé
        JPanel booksNorthPanel = new JPanel();
        BoxLayout northPanelLayout = new BoxLayout(booksNorthPanel, BoxLayout.X_AXIS);

        booksNorthPanel.setLayout(northPanelLayout);
        JButton addBookButton = new JButton("Könyv hozzáadása...");
        addBookButton.addActionListener(actionEvent -> showAddBookDialog());
        booksNorthPanel.add(addBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        JButton removeBookButton = new JButton("Könyv eltávolítása");
        removeBookButton.addActionListener(actionEvent -> showRemoveBookDialog());
        booksNorthPanel.add(removeBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());

        // keresés komponensei
        JCheckBox searchAuthorToo = new JCheckBox("Keresés a szerzők nevében is");
        searchAuthorToo.addActionListener(e -> {
            String searchBarText = searchBar.getText();
            if (!searchBarText.equals("Keresés " + library.books.size() + " könyv között"))
                bookTable.setRowSorter(library.search(searchBarText, searchAuthorToo.isSelected()));
        });
        // szűrés a kölcsönzött könyvekre
        JCheckBox showBorrowedBooksOnly = new JCheckBox("Csak a kölcsönzött könyvek mutatása");
        showBorrowedBooksOnly.addActionListener(actionEvent -> {
            if (showBorrowedBooksOnly.isSelected())
                bookTable.setRowSorter(library.showBorrowedOnly());
            else if (!searchBar.getText().equals(searchBar.getCurrentPlaceholderText()))
                bookTable.setRowSorter(library.search(searchBar.getText(), searchAuthorToo.isSelected()));
            else bookTable.setAutoCreateRowSorter(true);
        });
        booksNorthPanel.add(showBorrowedBooksOnly);

        // keresés mező
        this.searchBar = new BookSearchBar(library.books);
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String searchBarText = searchBar.getText();
                if (!searchBarText.equals(searchBar.getCurrentPlaceholderText())) {
                    bookTable.setRowSorter(library.search(searchBarText, searchAuthorToo.isSelected()));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // keresés az összes könyv között
                if (showBorrowedBooksOnly.isSelected())
                    showBorrowedBooksOnly.doClick();
                bookTable.setRowSorter(library.search(searchBar.getText(), searchAuthorToo.isSelected()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                bookTable.setRowSorter(library.search(searchBar.getText(), searchAuthorToo.isSelected()));
            }
        });
        booksNorthPanel.add(searchBar);
        booksNorthPanel.add(searchAuthorToo);

        booksPanel.add(booksNorthPanel, BorderLayout.NORTH);
        booksPanel.add(bookScrollPane, BorderLayout.CENTER);
        this.verticalSplitPane.add(booksPanel);

        // CellRendererek beállítása
        bookTable.setDefaultRenderer(String.class, new BookTableCellRenderer(bookTable.getDefaultRenderer(String.class)));
        bookTable.setDefaultRenderer(Integer.class, new BookTableCellRenderer(bookTable.getDefaultRenderer(Integer.class)));
        bookTable.setDefaultRenderer(Boolean.class, new BookTableCellRenderer(bookTable.getDefaultRenderer(Boolean.class)));
        bookTable.setDefaultRenderer(Member.class, new BookTableCellRenderer(bookTable.getDefaultRenderer(Member.class)));

        bookTable.setRowHeight(20);
        // TODO: https://stackoverflow.com/questions/13192419/setting-a-tooltip-for-a-value-from-jcomboboxs-items-as-celleditor-in-jtable
    }

    /**
     * Létrehozza és beállítja a könyvtári tagok megjelenítéséért felelős komponenseket
     */
    private void initMemberTable() {
        this.memberTable.setIntercellSpacing(new Dimension(10, 2)); // cellák ne folyjanak össze
        this.memberTable.setRowHeight(18);
        this.memberTable.setAutoCreateRowSorter(true);
        JScrollPane memberScrollPane = new JScrollPane(this.memberTable);

        // ha egy tag adataira kétszer kattintanak, megnyitjuk a szerkesztés párbeszédablakot
        this.memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Member member = library.members.get(memberTable.convertRowIndexToModel(memberTable.getSelectedRow()));
                    MemberPanel memberPanel = new MemberPanel();
                    memberPanel.setNameValue(member.getName());
                    memberPanel.setDateOfBirth(member.getDateOfBirth().toString());
                    memberPanel.setPhone(member.getPhone());
                    int chosenOption = JOptionPane.showConfirmDialog(null, memberPanel.getMainPanel(),
                            "Tag adatainak szerkesztése", JOptionPane.OK_CANCEL_OPTION);
                    if (chosenOption == JOptionPane.OK_OPTION)
                        if (!library.editMember(member, memberPanel.getNameValue(), memberPanel.getDateOfBirthValue(), memberPanel.getPhoneValue()))
                            JOptionPane.showMessageDialog(null, "Hibás adat került megadásra. Ellenőrizze, " +
                                    "hogy helyesen adta-e meg a formátumokat.", "Hibás adat", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // ha megváltozik a tagok valamilyen adata, a tagok adatait használó komponenseket frissítjük
        memberTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                library.bookData.fireTableDataChanged();
                reloadTree();
            }
        });

        // panel
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Könyvtári tagok"));
        JPanel membersComponentPanel = new JPanel();
        membersComponentPanel.setLayout(new BoxLayout(membersComponentPanel, BoxLayout.X_AXIS));
        JButton addMember = new JButton("Tag hozzáadása...");
        addMember.addActionListener(actionEvent -> {
            MemberPanel memberPanel = new MemberPanel();
            if (JOptionPane.showConfirmDialog(null, memberPanel.getMainPanel(),
                    "Tag hozzáadása", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                String name = memberPanel.getNameValue();
                String dob = memberPanel.getDateOfBirthValue();
                String phone = memberPanel.getPhoneValue();
                if (!library.addMember(name, dob, phone))
                    JOptionPane.showMessageDialog(null, "Hibás adat került megadásra. Ellenőrizze, " +
                            "hogy helyesen adta-e meg a formátumokat.", "Hibás adat", JOptionPane.WARNING_MESSAGE);
            }
        });
        membersComponentPanel.add(addMember);
        JButton removeMember = new JButton("Tag eltávolítása");
        removeMember.addActionListener(actionEvent -> {
            if (memberTable.getSelectedRow() >= 0) {
                int chosenOption = JOptionPane.showConfirmDialog(null, "Biztosan törli a kiválasztott " +
                        "tagot? Ha van kölcsönzött könyve, az törlődik.", "Biztosan törli?", JOptionPane.YES_NO_OPTION);
                if (chosenOption == JOptionPane.YES_OPTION)
                    library.remove(library.members.get(memberTable.convertRowIndexToModel(memberTable.getSelectedRow())));
            }
        });
        membersComponentPanel.add(removeMember);
        membersPanel.add(membersComponentPanel, BorderLayout.NORTH);

        membersPanel.add(memberScrollPane, BorderLayout.CENTER);
        this.horizontalSplitPane.add(membersPanel);
    }

    /**
     * Inicializálja a kölcsönzéseket nyilvántartó {@code JTree} objektumot.
     */
    private void initTree() {
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(this.library.members);
        BorrowData borrowData = new BorrowData(treeRoot, this.library.members);
        this.borrowersTree = new JTree(borrowData);
        this.borrowersTree.setRootVisible(false);
        this.borrowersTree.setShowsRootHandles(true);

        // Fa elemeinek az ikonjainak beállítása
        // TODO: ikonok lecserélése?
//        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
//        renderer.setOpenIcon(null);
//        renderer.setClosedIcon(null);
//        renderer.setLeafIcon(null);
//        borrowersTree.setCellRenderer(renderer);

        JScrollPane borrowersScrollPane = new JScrollPane(borrowersTree);
        this.northPanel.add(borrowersScrollPane);


        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createTitledBorder("Aktuális kölcsönzések"));
        treePanel.add(borrowersScrollPane, BorderLayout.CENTER);
        this.horizontalSplitPane.add(treePanel);
    }

    /**
     * Frissíti a kölcsönzéseket nyilvántartó fa struktúrát.
     */
    private void reloadTree() {
        if (ApplicationFrame.this.borrowersTree != null) {
            // TODO
//            DefaultTreeModel treeModel = (DefaultTreeModel) ApplicationFrame.this.borrowersTree.getModel();
//            treeModel.reload();
            ((BorrowData) borrowersTree.getModel()).reload();
            // mindig teljesen kinyitva jelenítjük meg
            for (int i = 0; i < ApplicationFrame.this.borrowersTree.getRowCount(); i++) {
                borrowersTree.expandRow(i);
            }
        }
    }

    /**
     * Frissíti a könyvek adatait tartalmazó táblázatot.
     */
    private void refreshBookTable() {
        this.library.bookData.fireTableDataChanged();
    }

    /**
     * Frissíti a tagok adatait tartalmazó táblázatot.
     */
    private void refreshMemberTable() {
        this.library.memberData.fireTableDataChanged();
    }

    /**
     * Frissíti az aktuális kölcsönzéseket megjelenítő fa nézetet és a tagokat, valamint a könyveket megjelenítő táblázatokat
     */
    private void refreshComponents() {
        refreshMemberTable();
        refreshBookTable();
        reloadTree();
    }

    /**
     * Beállítja azokat a {@code CellEditor}-okat, amiknek JComboBox-szal lehet megadni az értékét
     */
    private void initCellEditors() {
        // book table
        // kölcsönző JComboBox-szal való megadásához
        TableColumn memberColumn = bookTable.getColumnModel().getColumn(6);
        JComboBox<Member> memberComboBox = this.library.memberData.getMembersComboBox();
        memberComboBox.insertItemAt(null, 0); // ha korábban ki volt kölcsönözve, akkor a null-t választva lehet "visszavinni" TODO: gombbal is lehessen
        memberColumn.setCellEditor(new DefaultCellEditor(memberComboBox));

        // kategória JComboBox-szal való megadásához
        TableColumn categoryColumn = bookTable.getColumnModel().getColumn(3);
        JComboBox<String> categoryComboBox = new JComboBox<>();
        for (BookCategory category : BookCategory.values())
            categoryComboBox.addItem(category.getLocalizedName());
        categoryColumn.setCellEditor(new DefaultCellEditor(categoryComboBox));
    }

    /**
     * Konstruktor
     */
    public ApplicationFrame() {
        super("Könyvtárkezelő");
        this.library = new Library();
        this.library.serializationPath = "library.libdat";

        this.horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        this.horizontalSplitPane.setOneTouchExpandable(true);
        this.horizontalSplitPane.setContinuousLayout(true);
        this.verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.verticalSplitPane.add(this.horizontalSplitPane);
        this.verticalSplitPane.setOneTouchExpandable(true); // a két rész külön-külön megjeleníthető
        this.verticalSplitPane.setContinuousLayout(true);
        this.bookTable = new JTable();
        this.memberTable = new JTable();
        readDataFromFile();
        initFrame();
        initMenuBar();
        initBookTable();
        this.northPanel = new JPanel();
        this.northPanel.setLayout(new BoxLayout(this.northPanel, BoxLayout.X_AXIS));
        this.northPanel.setPreferredSize(new Dimension(1280, 250));
        this.northPanel.add(this.horizontalSplitPane);
        this.verticalSplitPane.add(this.northPanel);
        this.add(this.verticalSplitPane, BorderLayout.CENTER);
        initMemberTable();
        initTree();
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
