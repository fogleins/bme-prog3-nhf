import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.text.MaskFormatter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

public class ApplicationFrame extends JFrame {

    private Library library;

    private JTable bookTable;
    private JTable memberTable;
    private JPanel northPanel;
    private SearchBar searchBar;
    private JTree borrowersTree;


    private void readDataFromFile() {
        this.library = Library.readDataFromFile(this.library);
        this.bookTable.setModel(this.library.bookData);
        this.memberTable.setModel(this.library.memberData);
        initCellEditors();
    }

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
        // megnyitásra kattintva megnyitja a tallózási ablakot
        open.addActionListener(actionEvent -> showOpenFileDialog());
        JMenuItem save = new JMenuItem("Mentés");
        save.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
        // mentésre kattintva elmenti az adatokat
        save.addActionListener(actionEvent -> library.saveData());
        JMenuItem saveAs = new JMenuItem("Mentés másként...");
        saveAs.addActionListener(actionEvent -> showSaveAsDialog());
        JMenuItem exit = new JMenuItem("Kilépés");
        exit.setAccelerator(KeyStroke.getKeyStroke('W', CTRL_DOWN_MASK)); // TODO: action listener

        JMenu editMenu = new JMenu("Szerkesztés");

        JMenu libraryMenu = new JMenu("Könyvtár");
        JMenuItem addNew = new JMenuItem("Új könyv hozzáadása");
        addNew.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
        addNew.addActionListener(e -> {
            library.addBook();
            updateBookCount();
        });
        JMenuItem remove = new JMenuItem("Könyv eltávolítása");
        remove.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        remove.addActionListener(actionEvent -> {
            if (bookTable.getSelectedRow() >= 0) {
                library.removeBook(library.bookData.books.get(bookTable.convertRowIndexToModel(bookTable.getSelectedRow())));
                updateBookCount();
            }
        });

        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
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
        // ha változik a könyvek valamilyen adata, az aktuális kölcsönzések fáját is frissítjük
        this.bookTable.addPropertyChangeListener(propertyChangeEvent -> reloadTree());
        JScrollPane bookScrollPane = new JScrollPane(this.bookTable);

        // Panelek
        JPanel booksPanel = new JPanel();
        booksPanel.setBorder(BorderFactory.createTitledBorder("Könyvek"));
        booksPanel.setLayout(new BorderLayout());

        // komponenspanel a táblázat fölé
        JPanel booksNorthPanel = new JPanel();
        BoxLayout northPanelLayout = new BoxLayout(booksNorthPanel, BoxLayout.X_AXIS);

        booksNorthPanel.setLayout(northPanelLayout);
        JButton addBookButton = new JButton("Könyv hozzáadása...");
        addBookButton.addActionListener(actionEvent -> {
            library.addBook();
            updateBookCount();
        });
        booksNorthPanel.add(addBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        JButton removeBookButton = new JButton("Könyv eltávolítása");
        removeBookButton.addActionListener(actionEvent -> {
            if (bookTable.getSelectedRow() >= 0) {
                library.removeBook(library.bookData.books.get(bookTable.convertRowIndexToModel(bookTable.getSelectedRow())));
                updateBookCount();
            }
        });
        booksNorthPanel.add(removeBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        JCheckBox showBorrowedBooksOnly = new JCheckBox("Csak a kölcsönzött könyvek mutatása");
        booksNorthPanel.add(showBorrowedBooksOnly);  // TODO: action listener a toggle-re

        // keresés komponensei
        this.searchBar = new SearchBar(this.library, "Keresés " + library.bookData.books.size() + " könyv között");
        this.searchBar.addFocusListener(new FocusAdapter() { // a mezőbe kattintáskor eltűnik a szöveg
            @Override
            public void focusGained(FocusEvent e) {
                searchBar.setText("");
            }
        });
        this.searchBar.addFocusListener(new FocusAdapter() { // ha inaktív lesz a mező, a szöveg újból megjelenik
            @Override
            public void focusLost(FocusEvent e) {
                updateBookCount();
            }
        });
//        this.searchBar.addKeyListener(new KeyAdapter() { // karakter leütésekor frissül a keresés
//            @Override
//            public void keyTyped(KeyEvent e) { // TODO
//                System.out.println("key:\t" + Character.isLetter(e.getKeyChar()));
//                System.out.println("text:\t" + searchBar.getText());
//                String textboxContent = searchBar.getText();
//                if (Character.isLetter(e.getKeyChar()))
//                    library.search(bookTable, textboxContent + e.getKeyChar(), false);
//                else if (textboxContent.equals("")) {
//                    library.search(bookTable, "", false);
//                }
//            }
//        });
//        this.searchBar.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                String searchBarText = searchBar.getText();
//                if (searchBarText.equals(searchBar.getText())) {
//                    System.out.println(searchBarText);
//                    library.search(/*bookTable, */searchBarText, false);
//                }
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                String searchBarText = searchBar.getText();
//                if (searchBarText.equals(searchBar.getText())) {
//                    System.out.println(searchBar.getText());
//                    library.search(/*bookTable, */searchBar.getText(), false);
//                }
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                System.out.println(searchBar.getText());
//                library.search(bookTable, searchBar.getText(), false);
//            }
//        });
//        booksNorthPanel.add(this.searchBar);
        JCheckBox searchAuthorToo = new JCheckBox("Keresés a szerzők nevében is");
        JButton searchButton = new JButton("Keresés");
        searchButton.addActionListener(actionEvent -> {
            // TODO: keresés gomb eltávolítása?
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
     * Létrehozza és beállítja a könyvtári tagok megjelenítéséért felelős komponenseket
     */
    private void initMemberTable() {
        this.memberTable.setIntercellSpacing(new Dimension(10, 2)); // cellák ne folyjanak össze
        this.memberTable.setRowHeight(18);
        this.memberTable.setAutoCreateRowSorter(true);
        JScrollPane memberScrollPane = new JScrollPane(this.memberTable);
        this.northPanel.add(memberScrollPane); // TODO: ennek van jelentősége?
//        this.memberTable.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) { // TODO: egyszerűsítés, általánosítás
//                if (e.getClickCount() == 2) {
//                    int row;
//                    if ((row = memberTable.getSelectedRow()) > -1 && memberTable.getSelectedColumn() == 1) {
//
//
//                        JPanel panel = new JPanel(new BorderLayout(5, 5)); // a JOptionPane fő panele
//
//                        // Labelek létrehozása és panelhez adása
//                        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); // a JLabeleket tartalmazó panel
//                        label.add(new JLabel("Születési dátum (éééé-hh-nn): ", SwingConstants.RIGHT));
//                        panel.add(label, BorderLayout.WEST);
//
//                        JPanel input = new JPanel(new GridLayout(0, 1, 2, 2));
//                        JFormattedTextField dateOfBirthField = new JFormattedTextField();
//                        dateOfBirthField.setColumns(6);
//                        // alapértelmezetten a FormattedTextField margója nagyon keskeny, ami megnehezíti a MaskFormatter-rel
//                        // való együttműködést, ezért a margót kicsit bővítjük
//                        dateOfBirthField.setMargin(new Insets(0, 5, 0, 5));
//                        try {
//                            MaskFormatter dateMask = new MaskFormatter("####-##-##");
//                            dateMask.install(dateOfBirthField);
//                        } catch (ParseException ex) { // TODO
//                            System.out.println("nem parse-olható");
//                        }
//                        input.add(dateOfBirthField);
//                        panel.add(input, BorderLayout.CENTER);
//
//                        // JOptionPane-ben a TextField legyen fókuszban
//                        // TODO: mire kattintott?
//                        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
//                            @Override
//                            public void selectInitialValue() {
//                                dateOfBirthField.requestFocusInWindow();
//                            }
//                        };
//                        pane.createDialog(null, "Születési dátum szerkesztése").setVisible(true);
//
//
//                        // TODO: jó, de OptionDialog int chosenOption = JOptionPane.showOptionDialog(null, new Object[] {panel}, "decryptionKey", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] {"okCaption", "cancelCaption"}, null);
//
//
//                        int chosenOption = JOptionPane.showConfirmDialog(null, panel, "Születési dátum szerkesztése",
//                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
//                        String inp = dateOfBirthField.getText();
//
//                        if (inp != null && chosenOption == JOptionPane.OK_OPTION) {
//                            try {
//                                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                                library.memberData.members.get(memberTable.convertRowIndexToModel(row)).setDateOfBirth(LocalDate.parse(inp, dateFormatter));
//                                System.out.println(LocalDate.parse(inp, dateFormatter).isAfter(LocalDate.now()));
//                            } catch (DateTimeParseException parseException) {
//                                JOptionPane.showMessageDialog(null, "Hibás dátumformátumot adott meg. " +
//                                        "Használja az éééé-hh-nn formátumot.", "Hibás dátumformátum", JOptionPane.ERROR_MESSAGE);
//                            }
//                            library.memberData.fireTableDataChanged();
//                        }
//                    }
//                }
//            }
//        });
        // panel
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Könyvtári tagok"));
        JPanel membersComponentPanel = new JPanel();
        membersComponentPanel.setLayout(new BoxLayout(membersComponentPanel, BoxLayout.X_AXIS));
        JButton addMember = new JButton("Tag hozzáadása...");
        addMember.addActionListener(actionEvent -> {
            library.addMember();
            reloadTree();
        });
        membersComponentPanel.add(addMember);
        JButton removeMember = new JButton("Tag eltávolítása");
        removeMember.addActionListener(actionEvent -> {
            if (memberTable.getSelectedRow() >= 0) {
                library.removeMember(library.memberData.members.get(
                        memberTable.convertRowIndexToModel(memberTable.getSelectedRow())));
                reloadTree();
            }
        });
        membersComponentPanel.add(removeMember);
        membersPanel.add(membersComponentPanel, BorderLayout.NORTH);

        membersPanel.add(memberScrollPane, BorderLayout.CENTER);
        this.northPanel.add(membersPanel, BorderLayout.WEST);
    }

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
//        this.borrowersTree.setBackground(this.northPanel.getBackground());  // TODO: szöveg bg változtatása?


        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createTitledBorder("Aktuális kölcsönzések"));
        treePanel.add(borrowersScrollPane, BorderLayout.CENTER);
        this.northPanel.add(treePanel, BorderLayout.EAST);
    }

    private void reloadTree() {
        if (ApplicationFrame.this.borrowersTree != null) {
            DefaultTreeModel treeModel = (DefaultTreeModel) ApplicationFrame.this.borrowersTree.getModel();
            // TODO: ha nem nyitunk ki mindent, a változás után is ugyanazok legyenek kinyitva
//            int rows = ApplicationFrame.this.borrowersTree.getRowCount();
//            boolean[] isRowExpanded = new boolean[rows];
//            for (int i = 0; i < rows; i++)
//                isRowExpanded[i] = this.borrowersTree.isExpanded(i);
            treeModel.reload();
//            for (int i = 0; i < rows; i++) {
//                if (isRowExpanded[i] && !this.borrowersTree.isExpanded(i))
//                    borrowersTree.expandRow(i);
//            }
            for (int i = 0; i < ApplicationFrame.this.borrowersTree.getRowCount(); i++) {
                borrowersTree.expandRow(i);
            }
        }
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
     * Frissíti a könyvek számát a keresés mezőben
     */
    private void updateBookCount() {
        this.searchBar.setText("Keresés " + library.bookData.books.size() + " könyv között");
    }

    private LocalDate stringToLocalDate(String s) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(s, dateFormatter);
        } catch (DateTimeParseException parseException) {
            JOptionPane.showMessageDialog(null, "Hibás dátumformátumot adott meg. " +
                    "Használja az éééé-hh-nn formátumot.", "Hibás dátumformátum", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Konstruktor
     */
    public ApplicationFrame() {
        super("Könyvtárkezelő");
        this.library = new Library();
        this.library.serializationPath = "library.libdat";

        this.bookTable = new JTable();
        this.memberTable = new JTable();
        readDataFromFile();
        initFrame();
        initMenuBar();
        initBookTable();
        this.northPanel = new JPanel();
        this.northPanel.setLayout(new BoxLayout(this.northPanel, BoxLayout.X_AXIS));
        this.northPanel.setPreferredSize(new Dimension(1280, 250));
        this.add(northPanel, BorderLayout.NORTH);
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
