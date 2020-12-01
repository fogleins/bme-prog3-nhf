package library.impl;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * A tárolt adatokat összefogó osztály.
 */
public class Library implements Serializable {
    /**
     * A tárolt könyvek listája.
     */
    private final List<Book> books;

    /**
     * A tárolt tagok listája.
     */
    private final List<Member> members;


    /**
     * A könyvek adatait tároló táblázat modellje.
     */
    private transient BookData bookData;

    /**
     * A tagok adatait tartalmazó táblázat modellje.
     */
    private transient MemberData memberData;

    /**
     * A szerializált adatok mentési helye.
     */
    private transient String serializationPath;

    /**
     * Konstruktor
     */
    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    /**
     * Visszaadja a könyveket tartalmazó listát.
     *
     * @return A könyveket tartalmazó lista
     */
    public List<Book> getBooks() {
        return books;
    }

    /**
     * Visszaadja a tagokat tartalmazó listát.
     *
     * @return A tagokat tartalmazó lista.
     */
    public List<Member> getMembers() {
        return members;
    }

    /**
     * Visszaadja a könyveket tartalmazó táblázat modelljét.
     *
     * @return A könyveket tartalmazó táblázat modellje
     */
    public BookData getBookData() {
        return bookData;
    }

    /**
     * Visszaadja a tagokat tartalmazó táblázat modelljét.
     *
     * @return A tagokat tartalmazó táblázat modellje
     */
    public MemberData getMemberData() {
        return memberData;
    }

    /**
     * Visszaadja az adatok mentésének elérési útját.
     *
     * @return Az adatok mentésének elérési útja
     */
    public String getSerializationPath() {
        return serializationPath;
    }

    /**
     * Beállítja az adatmentés helyét.
     *
     * @param serializationPath Az új elérési út
     */
    public void setSerializationPath(String serializationPath) {
        this.serializationPath = serializationPath;
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
        } catch (IOException ex) {
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
     * @return A beolvasott {@code Library} objektum, ha a fájl nem található, új üres könyvtárat hoz létre
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
     * A könyvtárhoz ad egy könyvet, ha a megadott adatai helyesek.
     *
     * @param author       A könyv szerzője
     * @param title        A könyv címe
     * @param year         A könyv kiadási éve
     * @param category     A könyv típusa
     * @param language     A könyv nyelve
     * @param isBorrowable A könyv kölöcsönözhetősége
     * @return Igaz, ha a könyv adatai érvényesek voltak és a hozzáadás sikerült, egyébként hamis
     */
    public boolean addBook(String author, String title, String year, BookCategory category, String language, boolean isBorrowable) {
        if (ValidityChecker.isValidBookInput(author, title, year, language)) {
            Book book = new Book(author, title, Integer.parseInt(year), category, language, isBorrowable);
            this.bookData.addBook(book);
            return true;
        }
        return false;
    }

    /**
     * Eltávolít egy könyvet a programból.
     *
     * @param book Az eltávolítandó {@code Book} objektum
     */
    public void remove(Book book) {
        if (book == null)
            return;
        if (book.getBorrowedBy() != null)
            book.getBorrowedBy().getBorrowedBooks().remove(book);
        this.bookData.removeBook(book);
    }

    /**
     * Hozzáad egy tagot a programhoz.
     *
     * @param name  A tag neve
     * @param dob   A tag születési ideje
     * @param phone A tag telefonszáma
     */
    public boolean addMember(String name, String dob, String phone) {
        if (ValidityChecker.isValidMemberInput(name, dob, phone)) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBirth = LocalDate.parse(dob, dateFormatter);
            Member member = new Member(name, dateOfBirth, phone);
            this.memberData.add(member);
            return true;
        }
        return false;
    }

    /**
     * Szerkeszti egy tag adatait.
     *
     * @param member A tag, akinek az adatait módosítani szeretnénk
     * @param name   A tag új neve
     * @param dob    A tag új születési dátuma
     * @param phone  A tag új telefonszáma
     * @return Igaz, ha a módosítást sikerült végrehajtani
     */
    public boolean editMember(Member member, String name, String dob, String phone) {
        if (ValidityChecker.isValidMemberInput(name, dob, phone)) {
            member.setName(name);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            member.setDateOfBirth(LocalDate.parse(dob, dateFormatter));
            member.setPhone(phone);
            this.memberData.fireTableDataChanged();
            return true;
        }
        return false;
    }

    /**
     * Eltávolít egy tagot a programból.
     *
     * @param member Az eltávolítandó tag
     */
    public void remove(Member member) {
        if (member == null)
            return;
        List<Book> books = member.getBorrowedBooks();
        for (Book book : books)
            book.setBorrowedBy(null);
        this.memberData.remove(member);
    }
}
