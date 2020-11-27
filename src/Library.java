import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * A tárolt adatokat összefogó osztály.
 */
public class Library implements Serializable {
    /**
     * A tárolt könyvek listája.
     */
    List<Book> books;

    /**
     * A tárolt tagok listája.
     */
    List<Member> members;


    /**
     * A könyvek adatait tároló táblázat modellje.
     */
    transient BookData bookData;

    /**
     * A tagok adatait tartalmazó táblázat modellje.
     */
    transient MemberData memberData;

    /**
     * A szerializált adatok mentési helye.
     */
    transient String serializationPath;

    /**
     * Konstruktor
     */
    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    /**
     * Inicializálja a nem szerializált adattagokat.
     *
     * @param serializationPath A szerializálás elérési útvonala
     */
    public void initTransientVariables(String serializationPath) {
        this.bookData = new BookData(this.books);
        this.memberData = new MemberData(this.members);
        this.serializationPath = serializationPath;
    }

    /**
     * Szerializálja a könyvtár objektumot.
     *
     * @param serializationPath A szerializálás elérési útvonala
     */
    public void saveData(String serializationPath) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(serializationPath));
            outputStream.writeObject(this);
            outputStream.close();
        } catch (Exception ex) { // TODO
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hiba az adatok mentése során: A könyvek mentése sikertelen ("
                    + ex.getMessage() + ')', "Hiba az adatok mentése során", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Szerializálja a könyvtár objektumot
     */
    public void saveData() {
        saveData(this.serializationPath);
    }

    /**
     * Szerializálja a könyvtár objektumot a megadott helyre. Mentés előtt ellenőrzi, hogy létezik-e egyező nevű fájl a célkönyvtárban.
     * Ha igen, akkor megkérdezi a felhasználót, hogy felülírja-e.
     *
     * @param serializationPath A mentés helye
     */
    public void saveDataAs(String serializationPath) {
        // ha a felhasznnáló nem adta meg a kiterjesztést, akkor hozzáadjuk a fájlnévhez
        int extensionIndex = serializationPath.indexOf(".libdat");
        if (extensionIndex > 0) { // ha megtalálható a kiterjesztés a stringben, ellenőrizzük, hogy az a végén van-e
            String ext = serializationPath.substring(extensionIndex);
            if (!ext.equals(".libdat"))
                extensionIndex = -1; // ha nem a végén van, ugyanazt csináljuk, mintha nem is lenne benne
        }
        serializationPath = (extensionIndex == -1) ? serializationPath + ".libdat" : serializationPath;
        if (!new File(serializationPath).exists()) {
            this.serializationPath = serializationPath;
            this.saveData();
        } else {
            int chosenOption = JOptionPane.showConfirmDialog(null,
                    "A megadott helyen már létezik ilyen nevű fájl. Felülírja?", "Névütközés", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION) {
                this.serializationPath = serializationPath;
                this.saveData();
            }
        }
    }

    /**
     * Beolvas egy könyvtár objektumot adatfájlból.
     *
     * @param library A felülírandó {@code Library} objektum
     * @return A beolvasott {@code Library} objektum, ha a beolvasás nem sikerül, új üres könyvtárat hoz létre
     */
    public static Library readDataFromFile(Library library) {
        try {
            String path = library.serializationPath;
            ObjectInputStream libraryInputStream = new ObjectInputStream(new FileInputStream(path));
            library = (Library) libraryInputStream.readObject();
            libraryInputStream.close();
            library.initTransientVariables(path);
        } catch (FileNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(null, "A fájl nem található. Megnyithat egy fájlt a Fájl menü " +
                            "Megnyitás (CTRL+O) lehetőségét választva. Hibaüzenet: " + notFoundException.getMessage(),
                    "A fájl nem található", JOptionPane.WARNING_MESSAGE);

            library = new Library();
            library.initTransientVariables("library.libdat");
        } catch (StreamCorruptedException streamCorruptedException) {
            JOptionPane.showMessageDialog(null, "A megnyitni kívánt fájl sérült. Hibaüzenet: " + streamCorruptedException.getMessage(),
                    "A fájl nem található", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } catch (Exception exception) { // IOException vagy ClassNotFoundException
            JOptionPane.showMessageDialog(null, "Hiba a fájl beolvasása során. A program bezáródik. " +
                    "Hibaüzenet: " + exception.getMessage(), "A fájl nem található", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        return library;
    }

    /**
     * Megvalósítja a keresés funkciót a tárolt könyvek között.
     *
     * @param searchFor      A string, amit keresünk
     * @param searchInAuthor Igaz, ha a szerző nevében is szeretnénk keresni
     * @return A {@code RowSorter}, amit használva megjelennek a keresés eredményei
     */
    public RowSorter<BookData> search(String searchFor, boolean searchInAuthor) {
        RowFilter<BookData, Integer> bookFilter;
        if (!searchInAuthor) {
            bookFilter = new RowFilter<>() {
                public boolean include(Entry<? extends BookData, ? extends Integer> entry) {
                    Book book = bookData.books.get(entry.getIdentifier());
                    return book.getTitle().contains(searchFor);
                }
            };
        } else {
            bookFilter = new RowFilter<>() {
                public boolean include(Entry<? extends BookData, ? extends Integer> entry) {
                    Book book = bookData.books.get(entry.getIdentifier());
                    return book.getTitle().contains(searchFor) || book.getAuthor().contains(searchFor);
                }
            };
        }
        TableRowSorter<BookData> sorter = new TableRowSorter<>(bookData);
        sorter.setRowFilter(bookFilter);
        return sorter;
    }

    /**
     * A könyvek táblázatában csak a kölcsönzött könyveket mutatja.
     *
     * @return A {@code RowSorter}, amit használva csak a kölcsönzött könyvek lesznek láthatóak
     */
    public RowSorter<BookData> showBorrowedOnly() {
        return bookData.showBorrowedOnly();
    }

    /**
     * Megjelenít egy párbeszédablakot, amelyben megadhatók egy könyv adatai. Ha az adatok helyesek, hozzáadja a programhoz.
     * Ha hibás adatokat adott meg a felhasználó, hibaüzenet jelenik meg.
     */
    public void addBook() {
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
        JTextField author = new JTextField(20);
        JTextField title = new JTextField(20);
        JTextField year = new JTextField(20);
        JComboBox<String> category = new JComboBox<>();
        for (BookCategory cat : BookCategory.values())
            category.addItem(cat.getLocalizedName());
        JTextField language = new JTextField("magyar", 20);
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

        int chosenOption = JOptionPane.showConfirmDialog(null, panel, "Új könyv felvétele", JOptionPane.OK_CANCEL_OPTION);
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
                Book newBook = new Book(author.getText(), title.getText(), yearOfPublication,
                        BookCategory.valueOf((String) category.getSelectedItem(), "HU"),
                        language.getText().toLowerCase(), chosenRadioButton.equals("Igen"));
                this.bookData.addBook(newBook);
            } catch (MissingRequiredArgumentException argumentException) {
                JOptionPane.showMessageDialog(null, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    /**
     * Eltávolít egy könyvet a programból.
     *
     * @param book Az eltávolítandó {@code Book} objektum
     */
    public void removeBook(Book book) {
        if (book == null)
            return;
        int chosenOption = JOptionPane.showConfirmDialog(null,
                (book.getBorrowedBy() == null) ? "Biztosan törli a kiválasztott könyvet?" : "A kiválasztott könyvet kikölcsönözték. Biztosan törli?",
                "Biztosan törli?", JOptionPane.YES_NO_OPTION);
        if (chosenOption == JOptionPane.YES_OPTION) {
            if (book.getBorrowedBy() != null)
                book.getBorrowedBy().getBorrowedBooks().remove(book);
            this.bookData.removeBook(book);
        }
    }
}
