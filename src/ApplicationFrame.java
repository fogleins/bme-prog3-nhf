import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.List;

public class ApplicationFrame extends JFrame {

    private BookData bookData;
    private AuthorData authorData;
    private MemberData memberData;

    private JPanel northPanel;
    private JButton addBookButton;
    private JButton removeBookButton;

    private void saveData() {
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

    // TODO: kisebb részekre bontani?
    private void addNewBook() {
        JPanel panel = new JPanel(new BorderLayout(5, 5)); // a JOptionPane fő panele

        // Labelek létrehozása és panelhez adása
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); // a JLabeleket tartalmazó panel
        label.add(new JLabel("Szerző", SwingConstants.RIGHT));
        label.add(new JLabel("Cím", SwingConstants.RIGHT));
        label.add(new JLabel("Kiadás éve", SwingConstants.RIGHT));
        label.add(new JLabel("Kategória", SwingConstants.RIGHT));
        label.add(new JLabel("Nyelv", SwingConstants.RIGHT));
        label.add(new JLabel("Kölcsönözhető?", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        // a felhasználó által szerkeszthető komponensek létrehozása és panelhez adása
        JPanel input = new JPanel(new GridLayout(0, 1, 2, 2));
        JComboBox<Author> author = new JComboBox<>();
        for (Author a : authorData.authors)
            author.addItem(a);
        JTextField title = new JTextField();
        JTextField year = new JTextField();
        JComboBox<String> category = new JComboBox<>();
        for (BookCategory cat : BookCategory.values())
            category.addItem(cat.getLocalizedName());
        JTextField language = new JTextField("magyar");
        ButtonGroup borrowableButtons = new ButtonGroup();
        JRadioButton yes = new JRadioButton("Igen");
        JRadioButton no = new JRadioButton("Nem", true);
        JPanel buttons = new JPanel(); // az Igen-Nem lehetőségek panele
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(yes);
        buttons.add(no);
        borrowableButtons.add(yes);
        borrowableButtons.add(no);
        input.add(author);
        input.add(title);
        input.add(year);
        input.add(category);
        input.add(language);
        input.add(buttons, BorderLayout.WEST);
        panel.add(input, BorderLayout.CENTER);

        int chosenOption = JOptionPane.showConfirmDialog(this, panel, "Új könyv felvétele", JOptionPane.OK_CANCEL_OPTION);
        if (chosenOption == JOptionPane.OK_OPTION) {
            try {
                // booleanná alakítás
                String chosenRadioButton = "";
                for (Enumeration<AbstractButton> buttonsEnum = borrowableButtons.getElements(); buttonsEnum.hasMoreElements(); ) {
                    AbstractButton button = buttonsEnum.nextElement();

                    if (button.isSelected()) {
                        chosenRadioButton = button.getText();
                        break;
                    }
                }

                // kiadási év intté alakítása
                int yearOfPublication;
                try {
                    yearOfPublication = Integer.parseInt(year.getText());
                } catch (NumberFormatException numberFormatException) {
                    yearOfPublication = 0;
                }

                // könyv létrehozása és gyűjteményhez adása
                Book newBook = new Book((Author) author.getSelectedItem(), title.getText(), yearOfPublication,
                        BookCategory.valueOf((String) category.getSelectedItem(), "HU"),
                        language.getText().toLowerCase(), chosenRadioButton.equals("Igen"));
                this.bookData.addBook(newBook);
            } catch (MissingRequiredArgumentException argumentException) {
                JOptionPane.showMessageDialog(this, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    private void removeBook(Book book) {
        if (book == null)
            return;
        try {
            int chosenOption = JOptionPane.showConfirmDialog(this, "Biztosan törli a kiválasztott könyvet?",
                    "Biztosan törli?", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION)
                bookData.removeBook(book);
        } catch (BookNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(this, "A megadott könyv nincs a tárolt könyvek között. " +
                    "A gyűjtemény nem került módosításra.", "A könyv nem található", JOptionPane.ERROR_MESSAGE);
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
            bookData = new BookData();
            authorData = new AuthorData();
            memberData = new MemberData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // adatok mentése kilépéskor
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
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
        JMenuItem exit = new JMenuItem("Kilépés");
        JMenuItem save = new JMenuItem("Mentés");

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exit);

        JMenu edit = new JMenu("Szerkesztés");

        JMenu library = new JMenu("Könyvtár");
        JMenuItem addNew = new JMenuItem("Új könyv hozzáadása");
        JMenuItem remove = new JMenuItem("Könyv eltávolítása");

        ApplicationFrame parent = this;
        addNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewBook();
            }
        });

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
        bookTable.setIntercellSpacing(new Dimension(10, 5)); // cellák ne folyjanak össze
        bookTable.setAutoCreateRowSorter(true);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);

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
                addNewBook();
            }
        });
        booksNorthPanel.add(this.addBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        this.removeBookButton = new JButton("Könyv eltávolítása");
        this.removeBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookTable.getSelectedRow() >= 0)
                    removeBook(bookData.books.get(bookTable.convertRowIndexToModel(bookTable.getSelectedRow())));
            }
        });
        booksNorthPanel.add(this.removeBookButton);
        booksNorthPanel.add(Box.createHorizontalGlue());
        JCheckBox showBorrowedBooksOnly = new JCheckBox("Csak a kölcsönzött könyvek mutatása");
        booksNorthPanel.add(showBorrowedBooksOnly);

        // keresés komponensei
        JFormattedTextField searchBar = new JFormattedTextField("Keresés " + bookData.books.size() + " könyv között");
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
        // TODO: https://stackoverflow.com/questions/13192419/setting-a-tooltip-for-a-value-from-jcomboboxs-items-as-celleditor-in-jtable
    }

    /**
     * Létrehozza és beállítja a szerzők megjelenítéséért felelős komponenseket
     */
    private void initAuthorTable() {
        JTable authorTable = new JTable(authorData);
        authorTable.setIntercellSpacing(new Dimension(10, 5)); // cellák ne folyjanak össze
        authorTable.setRowHeight(18);
        authorTable.setAutoCreateRowSorter(true);
        JScrollPane authorScrollPane = new JScrollPane(authorTable);
        // panel
        JPanel authorsPanel = new JPanel();
        authorsPanel.setBorder(BorderFactory.createTitledBorder("Szerzők"));
        authorsPanel.setLayout(new BorderLayout());
        authorsPanel.add(authorScrollPane);
        this.northPanel.add(authorsPanel);
    }

    /**
     * Létrehozza és beállítja a könyvtári tagok megjelenítéséért felelős komponenseket
     */
    private void initMemberTable() {
        JTable memberTable = new JTable(memberData);
        memberTable.setIntercellSpacing(new Dimension(10, 2)); // cellák ne folyjanak össze
        memberTable.setRowHeight(18);
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
