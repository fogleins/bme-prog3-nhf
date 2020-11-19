import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/** A tárolt adatokat összefogó osztály */
public class Library implements Serializable {
    List<Book> books;
    List<Author> authors;
    List<Member> members;

    transient BookData bookData;
    transient AuthorData authorData;
    transient MemberData memberData;
    transient String serializationPath;

    public Library() {
        this.books = new ArrayList<>();
        this.authors = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void initTransientVariables(String serializationPath) {
        this.bookData = new BookData(this.books);
        this.authorData = new AuthorData(this.authors);
        this.memberData = new MemberData(this.members);
        this.serializationPath = serializationPath;
    }

    /**
     * Szerializálja a könyvtár objektumot
     */
    public void saveData() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(this.serializationPath));
            outputStream.writeObject(this);
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hiba az adatok mentése során: A könyvek mentése sikertelen ("
                    + ex.getMessage() + ')', "Hiba az adatok mentése során", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Beolvas egy könyvtár objektumot adatfájlból
     * @param library A felülírandó {@code Library} objektum
     * @return A beolvasott {@code Library} objektum, ha a beolvasás nem sikerül, új üres könyvtárat hoz létre
     */
    public static Library readDataFromFile(/*String serializationPath*/Library library) {
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
        }
        catch (Exception ex) { // TODO: exception handling
            ex.printStackTrace();
            library = new Library();
            library.initTransientVariables("library.libdat");
        }
        return library;
    }

    /**
     * Megjelenít egy párbeszédablakot, amelyben megadhatók egy szerző adatai. Ha hibás adatokat ad meg a felhasználó,
     * hibaüzenet jelenik meg.
     */
    public void addAuthor() {
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

        int chosenOption = JOptionPane.showConfirmDialog(null, panel, "Új szerző hozzáadása", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(null, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    /**
     * Eltávolít egy szerzőt a programból.
     * @param author Az eltávolítandó {@code Author} objektum
     */ // TODO: ellenőrizze, hogy a megadott szerzőnek vannak-e tárolva könyvei
    public void removeAuthor(Author author) {
        if (author == null)
            return;
//        try {
        int chosenOption = JOptionPane.showConfirmDialog(null, "Biztosan törli a kiválasztott szerzőt?",
                "Biztosan törli?", JOptionPane.YES_NO_OPTION);
        if (chosenOption == JOptionPane.YES_OPTION)
            this.authorData.removeAuthor(author);
//        } catch (BookNotFoundException notFoundException) {
//            JOptionPane.showMessageDialog(this, "A megadott könyv nincs a tárolt könyvek között. " +
//                    "A gyűjtemény nem került módosításra.", "A könyv nem található", JOptionPane.ERROR_MESSAGE);
//        }
    }

    /**
     * Megjelenít egy párbeszédablakot, amelyben megadhatók egy tag adatai, ha helyesek az adatok, hozzáadja a programhoz.
     * Ha hibás adatokat ad meg a felhasználó, hibaüzenetet jelenít meg.
     */
    public void addMember() {
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

        int chosenOption = JOptionPane.showConfirmDialog(null, panel, "Új könyvtári tag hozzáadása", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(null, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    /**
     * Eltávolít egy könyvtári tagot a programból.
     * @param member Az eltávolítandó {@code Member} objektum
     */
    public void removeMember(Member member) {
        if (member == null)
            return;
        int chosenOption = JOptionPane.showConfirmDialog(null, "Biztosan törli a kiválasztott tagot?",
                "Biztosan törli?", JOptionPane.YES_NO_OPTION);
        if (chosenOption == JOptionPane.YES_OPTION)
            this.memberData.removeMember(member);
    }

    /**
     * Megjelenít egy párbeszédablakot, amelyben megadhatók egy könyv adatai. Ha az adatok helyesek, hozzáadja a programhoz.
     * Ha hobás adatokat adott meg a felhasználó, hibaüzenet jelenik meg.
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
        JComboBox<Author> author = new JComboBox<>();
        for (Author a : this.authors)
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
                Book newBook = new Book((Author) author.getSelectedItem(), title.getText(), yearOfPublication,
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
     * @param book Az eltávolítandó {@code Book} objektum
     */
    public void removeBook(Book book) {
        if (book == null)
            return;
        try {
            int chosenOption = JOptionPane.showConfirmDialog(null, "Biztosan törli a kiválasztott könyvet?",
                    "Biztosan törli?", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION)
                this.bookData.removeBook(book);
        } catch (BookNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(null, "A megadott könyv nincs a tárolt könyvek között. " +
                    "A gyűjtemény nem került módosításra.", "A könyv nem található", JOptionPane.ERROR_MESSAGE);
        }
    }
}
