import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

public class ApplicationFrame extends JFrame {

    private Library library;
    private String serializationPath;

    private BookData bookData;
    private AuthorData authorData;
    private MemberData memberData;

    private JTable bookTable;
    private JTable authorTable;
    private JTable memberTable;
    private JPanel northPanel;
    private JButton addBookButton;
    private JButton removeBookButton;

    private void saveData() {
        try {
            library.books = bookData.books;
            library.authors = authorData.authors;
            library.members = memberData.members;
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(this.serializationPath));
            outputStream.writeObject(library);
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
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
        for (Author a : this.authorData.authors)
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

    private void removeBook() {
        Book book = this.bookData.books.get(this.bookTable.convertRowIndexToModel(this.bookTable.getSelectedRow()));
        if (book == null)
            return;
        try {
            int chosenOption = JOptionPane.showConfirmDialog(this, "Biztosan törli a kiválasztott könyvet?",
                    "Biztosan törli?", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION)
                this.bookData.removeBook(book);
        } catch (BookNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(this, "A megadott könyv nincs a tárolt könyvek között. " +
                    "A gyűjtemény nem került módosításra.", "A könyv nem található", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewAuthor() {
        JPanel panel = new JPanel(new BorderLayout(5, 5)); // a JOptionPane fő panele

        // Labelek létrehozása és panelhez adása
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); // a JLabeleket tartalmazó panel
        label.add(new JLabel("Név", SwingConstants.RIGHT));
        label.add(new JLabel("Születési év", SwingConstants.RIGHT));
        label.add(new JLabel("Anyanyelv", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        // a felhasználó által szerkeszthető komponensek létrehozása és panelhez adása
        JPanel input = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField name = new JTextField();
        JTextField year = new JTextField();
        JComboBox<String> language = new JComboBox<>();
        for (NativeLanguage lang : NativeLanguage.values())
            language.addItem(lang.getLocalizedName());

        input.add(name);
        input.add(year);
        input.add(language);
        panel.add(input, BorderLayout.CENTER);

        int chosenOption = JOptionPane.showConfirmDialog(this, panel, "Új szerző hozzáadása", JOptionPane.OK_CANCEL_OPTION);
        if (chosenOption == JOptionPane.OK_OPTION) {
            try {
                // születési év intté alakítása
                int birthyear;
                try {
                    birthyear = Integer.parseInt(year.getText());
                } catch (NumberFormatException numberFormatException) {
                    birthyear = 0;
                }

                Author newAuthor = new Author(name.getText(), birthyear, NativeLanguage.valueOf((String) language.getSelectedItem(), "HU"));
                this.authorData.addAuthor(newAuthor);
            } catch (MissingRequiredArgumentException argumentException) {
                JOptionPane.showMessageDialog(this, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    private void removeAuthor() {
        if (authorTable.getSelectedRow() < 0) // ha nincs kijelölve sor, nem csinálunk semmit
            return;
        Author author = this.authorData.authors.get(this.authorTable.convertRowIndexToModel(this.authorTable.getSelectedRow()));
        if (author == null)
            return;
//        try {
        int chosenOption = JOptionPane.showConfirmDialog(this, "Biztosan törli a kiválasztott szerzőt?",
                "Biztosan törli?", JOptionPane.YES_NO_OPTION);
        if (chosenOption == JOptionPane.YES_OPTION)
            this.authorData.removeAuthor(author);
//        } catch (BookNotFoundException notFoundException) {
//            JOptionPane.showMessageDialog(this, "A megadott könyv nincs a tárolt könyvek között. " +
//                    "A gyűjtemény nem került módosításra.", "A könyv nem található", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void addNewMember() {
        JPanel panel = new JPanel(new BorderLayout(5, 5)); // a JOptionPane fő panele

        // Labelek létrehozása és panelhez adása
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); // a JLabeleket tartalmazó panel
        label.add(new JLabel("Név", SwingConstants.RIGHT));
        label.add(new JLabel("Születési év", SwingConstants.RIGHT));
        label.add(new JLabel("Telefonszám", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        // a felhasználó által szerkeszthető komponensek létrehozása és panelhez adása
        JPanel input = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField name = new JTextField();
        JTextField year = new JTextField();
        JTextField phone = new JTextField();
        input.add(name);
        input.add(year);
        input.add(phone);
        panel.add(input, BorderLayout.CENTER);

        int chosenOption = JOptionPane.showConfirmDialog(this, panel, "Új könyvtári tag hozzáadása", JOptionPane.OK_CANCEL_OPTION);
        if (chosenOption == JOptionPane.OK_OPTION) {
            try {
                // születési év intté alakítása
                int birthyear;
                try {
                    birthyear = Integer.parseInt(year.getText());
                } catch (NumberFormatException numberFormatException) {
                    birthyear = 0;
                }

                Member newMember = new Member(name.getText(), birthyear, phone.getText());
                this.memberData.addMember(newMember);
            } catch (MissingRequiredArgumentException argumentException) {
                JOptionPane.showMessageDialog(this, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    private void removeMember() {
        if (memberTable.getSelectedRow() < 0) // ha nincs kiválasztott sor, visszatérünk
            return;
        Member member = this.memberData.members.get(this.memberTable.convertRowIndexToModel(this.memberTable.getSelectedRow()));
        if (member == null)
            return;
        int chosenOption = JOptionPane.showConfirmDialog(this, "Biztosan törli a kiválasztott tagot?",
                "Biztosan törli?", JOptionPane.YES_NO_OPTION);
        if (chosenOption == JOptionPane.YES_OPTION)
            this.memberData.removeMember(member);
    }

    private void readDataFromFile() {
        this.library = new Library();
        try {
            ObjectInputStream libraryInputStream = new ObjectInputStream(new FileInputStream(this.serializationPath));
            this.library = (Library) libraryInputStream.readObject();
            libraryInputStream.close();
        } catch (FileNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(null, "A fájl nem található. Megnyithat egy fájlt a Fájl menü " +
                            "Megnyitás (CTRL+O) lehetőségét választva. Hibaüzenet: " + notFoundException.getMessage(),
                    "A fájl nem található", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) { // TODO: exception handling
            ex.printStackTrace();
        } finally {
            this.bookData = new BookData(library.books);
            this.authorData = new AuthorData(library.authors);
            this.memberData = new MemberData(library.members);

            this.bookTable.setModel(this.bookData);
            this.authorTable.setModel(this.authorData);
            this.memberTable.setModel(this.memberData);
            initCellEditors();
        }
    }

    private void showOpenFileDialog() {
        JFileChooser chooser = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("libdat adatfájlok", "libdat");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.serializationPath = chooser.getSelectedFile().getPath();
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
                saveData();
            }
        });
    }

    /**
     * Létrehozza, beállítja és az ablakhoz adja a menüsort
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("Fájl");
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
                saveData();
            }
        });

        JMenu edit = new JMenu("Szerkesztés");

        JMenu library = new JMenu("Könyvtár");
        JMenuItem addNew = new JMenuItem("Új könyv hozzáadása");
        addNew.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
        addNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewBook();
            }
        });
        JMenuItem remove = new JMenuItem("Könyv eltávolítása");
        remove.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bookTable.getSelectedRow() >= 0)
                    removeBook();
            }
        });

        file.add(open);
        file.add(save);
        file.addSeparator();
        file.add(exit);

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
                    removeBook();
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
        authorTable.setIntercellSpacing(new Dimension(10, 5)); // cellák ne folyjanak össze
        authorTable.setRowHeight(18);
        authorTable.setAutoCreateRowSorter(true);
        JScrollPane authorScrollPane = new JScrollPane(authorTable);
        // panel
        JPanel authorsPanel = new JPanel(new BorderLayout());
        authorsPanel.setBorder(BorderFactory.createTitledBorder("Szerzők"));
        JPanel authorsComponentPanel = new JPanel();
        authorsComponentPanel.setLayout(new BoxLayout(authorsComponentPanel, BoxLayout.X_AXIS));
        JButton addAuthor = new JButton("Szerző hozzáadása...");
        addAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewAuthor();
            }
        });
        authorsComponentPanel.add(addAuthor);
        JButton removeAuthor = new JButton("Szerző eltávolítása");
        removeAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAuthor();
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
        memberTable.setIntercellSpacing(new Dimension(10, 2)); // cellák ne folyjanak össze
        memberTable.setRowHeight(18);
        memberTable.setAutoCreateRowSorter(true);
        JScrollPane memberScrollPane = new JScrollPane(memberTable);
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
                addNewMember();
            }
        });
        membersComponentPanel.add(addMember);
        JButton removeMember = new JButton("Tag eltávolítása");
        removeMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeMember();
            }
        });
        membersComponentPanel.add(removeMember);
        membersPanel.add(membersComponentPanel, BorderLayout.NORTH);

        membersPanel.add(memberScrollPane, BorderLayout.CENTER);
        this.northPanel.add(membersPanel, BorderLayout.CENTER);
    }

    private void initCellEditors() {
        // book table
        // kölcsönző JComboBox-szal való megadsához
        TableColumn memberColumn = bookTable.getColumnModel().getColumn(6);
        JComboBox<Member> memberComboBox = this.memberData.getMembersComboBox();
        memberColumn.setCellEditor(new DefaultCellEditor(memberComboBox));

        // kategória JComboBox-szal való megadsához
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
        this.serializationPath = "library.libdat";
        this.library = new Library();
        this.bookData = new BookData();
        this.authorData = new AuthorData();
        this.memberData = new MemberData();

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
