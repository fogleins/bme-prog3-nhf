import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

/**
 * A tárolt adatokat összefogó osztály
 */
public class Library implements Serializable {
    List<Book> books;
    List<Member> members;

    transient BookData bookData;
    transient MemberData memberData;
    transient String serializationPath;

    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void initTransientVariables(String serializationPath) {
        this.bookData = new BookData(this.books);
        this.memberData = new MemberData(this.members);
        this.serializationPath = serializationPath;
    }

    /**
     * Szerializálja a könyvtár objektumot
     */
    public void saveData(String serializationPath) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(serializationPath));
            outputStream.writeObject(this);
            outputStream.close();
        } catch (Exception ex) {
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
     * Beolvas egy könyvtár objektumot adatfájlból
     *
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
        } catch (Exception ex) { // TODO: exception handling
            ex.printStackTrace();
            library = new Library();
            library.initTransientVariables("library.libdat");
        }
        return library;
    }

    /**
     * Megjelenít egy párbeszédablakot, amelyben megadhatók egy tag adatai, ha helyesek az adatok, hozzáadja a programhoz.
     * Ha hibás adatokat ad meg a felhasználó, hibaüzenetet jelenít meg.
     */
    public void addMember() { // TODO: GUI-s részek elkülönítése
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
        JFormattedTextField dateOfBirthField = new JFormattedTextField();
        try {
            MaskFormatter dateMask = new MaskFormatter("####-##-##");
            dateMask.install(dateOfBirthField);
        } catch (ParseException ex) { // TODO
            System.out.println("nem parse-olható");
        }
        JTextField phone = new JTextField("06301234567");
        phone.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String phoneFieldValue = phone.getText();
                if (phoneFieldValue.equals("06301234567"))
                    phone.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                String phoneFieldValue = phone.getText();
                if (phoneFieldValue.equals("") || !phoneFieldValue.matches("\\d+")) {
                    phone.setText("06301234567");
                }
            }
        });
        input.add(name);
        input.add(dateOfBirthField);
        input.add(phone);
        panel.add(input, BorderLayout.CENTER);

        int chosenOption = JOptionPane.showConfirmDialog(null, panel, "Új könyvtári tag hozzáadása", JOptionPane.OK_CANCEL_OPTION);
        if (chosenOption == JOptionPane.OK_OPTION) {
            try {
                // születési idő dátummá alakítása
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfBirth = LocalDate.parse(dateOfBirthField.getText(), dateFormatter);
                // adatok felvétele
                Member newMember = new Member(name.getText(), dateOfBirth, phone.getText(), LocalDate.now());
                this.memberData.addMember(newMember);
            } catch (DateTimeParseException parseException) {
                JOptionPane.showMessageDialog(null, "Hibás dátumformátumot adott meg. " +
                        "Használja az éééé-hh-nn formátumot.", "Hibás dátumformátum", JOptionPane.ERROR_MESSAGE);
            } catch (MissingRequiredArgumentException argumentException) {
                JOptionPane.showMessageDialog(null, "Hibás adatokat adott meg.", "Hibás adat", JOptionPane.ERROR_MESSAGE);
            } catch (PersonAlreadyAddedException alreadyAddedException) {
                JOptionPane.showMessageDialog(null, alreadyAddedException.getMessage(),
                        "A felvenni kívánt tag már szerepel a programban", JOptionPane.WARNING_MESSAGE);
            } catch (Exception exception) { // TODO: exception kezelése
                exception.printStackTrace();
            }
        }
    }

    /**
     * Eltávolít egy könyvtári tagot a programból.
     *
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
        JTextField author = new JTextField();
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
        try {
            int chosenOption = JOptionPane.showConfirmDialog(null,
                    (book.getBorrowedBy() == null) ? "Biztosan törli a kiválasztott könyvet?" : "A kiválasztott könyvet kikölcsönözték. Biztosan törli?",
                    "Biztosan törli?", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION) {
                if (book.getBorrowedBy() != null)
                    book.getBorrowedBy().getBorrowedBooks().remove(book);
                this.bookData.removeBook(book);
            }
        } catch (BookNotFoundException notFoundException) {
            JOptionPane.showMessageDialog(null, "A megadott könyv nincs a tárolt könyvek között. " +
                    "A gyűjtemény nem került módosításra.", "A könyv nem található", JOptionPane.ERROR_MESSAGE);
        }
    }
}
