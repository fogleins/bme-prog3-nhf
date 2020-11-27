import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

class MemberManager {
    /**
     * Létrehoz egy tagok adatai megadását lehetővé tevő panelt a szükséges komponensekkel és lehetővé teszi a komponensek későbbi elérését és szerkesztését
     */
    private static class MemberPanel extends JPanel {
        JPanel mainPanel;
        JTextField name;
        JFormattedTextField dateOfBirth;
        JTextField phone;

        /**
         * Létrehoz egy tagok adatainak megadását lehetővé tevő panelt és inicializálja a komponenseit
         */
        MemberPanel() {
            this.mainPanel = new JPanel(new BorderLayout(5, 5)); // a JOptionPane fő panele

            // Labelek létrehozása és panelhez adása
            JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); // a JLabeleket tartalmazó panel
            label.add(new JLabel("Név", SwingConstants.RIGHT));
            label.add(new JLabel("Születési idő", SwingConstants.RIGHT));
            label.add(new JLabel("Telefonszám", SwingConstants.RIGHT));
            this.mainPanel.add(label, BorderLayout.WEST);

            // a felhasználó által szerkeszthető komponensek létrehozása és panelhez adása
            JPanel input = new JPanel(new GridLayout(0, 1, 2, 2));
            this.name = new JTextField();
            this.dateOfBirth = new JFormattedTextField();
            try {
                MaskFormatter dateMask = new MaskFormatter("####-##-##");
                dateMask.install(dateOfBirth);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Hibás dátumformátum. Használja az éééé-hh-nn formátumot.", "Hibás formátum", JOptionPane.WARNING_MESSAGE);
            }
            // belekattintáskor a kurzort a TextBox elejére állítjuk, így segítve a dátum helyes megadását; ld. komment a margók beállításánál
            this.dateOfBirth.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    dateOfBirth.setCaretPosition(0);
                }
            });
            this.phone = new JTextField();

            // a TextField-ek margóját kicsit megnöveljük, ez nagyban segíti a dátum helyes megadását, ugyanis alapértelmezetten
            // a margók elég keskenyek (0), és a születési dátum megadásakor könnyű nem az első indexre helyezni a kurzort,
            // az így beírt adatokat viszont a MaskFormatter nem tekinti érvényesnek, mert az csak számokat tartalmazhat, szóközöket nem
            this.name.setMargin(new Insets(0, 3, 0, 5));
            this.dateOfBirth.setMargin(new Insets(0, 3, 0, 5));
            this.phone.setMargin(new Insets(0, 3, 0, 5));
            input.add(name);
            input.add(dateOfBirth);
            input.add(phone);
            this.mainPanel.add(input, BorderLayout.CENTER);
        }
    }

    /**
     * Megvizsgálja, hogy a megadott adatok érvényesek-e, lehetnek-e egy {@code Member} adatai
     *
     * @param memberName        A felhasználó által megadott név
     * @param memberDateOfBirth A felhasználó által megadott születési idő
     * @param memberPhone       A felhasználó által megadott telefonszám
     * @return Hamis, ha valamelyik paraméter nem felel meg az elvárt értéknek, egyébként igaz
     */
    private static boolean isValidInput(String memberName, String memberDateOfBirth, String memberPhone) {
        if (memberName.equals("") || !(memberPhone.matches("\\d+") || memberPhone.equals("")))
            return false;
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(memberDateOfBirth, dateFormatter);
        } catch (DateTimeParseException parseException) {
            return false;
        }
        return true;
    }

    /**
     * Lehetővé teszi egy tag adatainak szerkesztését
     *
     * @param member A szerkesztendő tag
     */
    static void editMemberDialog(Member member) {
        MemberPanel memberPanel = new MemberPanel();
        memberPanel.name.setText(member.getName());
        memberPanel.dateOfBirth.setText(member.getDateOfBirth().toString());
        memberPanel.phone.setText(member.getPhone());
        int chosenOption = JOptionPane.showConfirmDialog(null, memberPanel.mainPanel, "Tag adatainak szerkesztése", JOptionPane.OK_CANCEL_OPTION);
        if (chosenOption == JOptionPane.OK_OPTION) {
            String name = memberPanel.name.getText();
            String dob = memberPanel.dateOfBirth.getText();
            String phone = memberPanel.phone.getText();
            if (isValidInput(name, dob, phone)) {
                member.setName(name);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                member.setDateOfBirth(LocalDate.parse(dob, dateFormatter));
                member.setPhone(phone);
            } else
                JOptionPane.showMessageDialog(null, "Hibás adat került megadásra. Ellenőrizze, " +
                        "hogy helyesen adta-e meg a formátumokat.", "Hibás adat", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Lehetővé teszi egy új tag létrehozását grafikus felületen keresztül. Ha helyesek a megadott adatok, hozzáadja a tagot a programhoz
     *
     * @param library A könyvtár objektum, aminek a tagjai közé felvesszük az új tagot
     */
    static void addMemberDialog(Library library) {
        MemberPanel memberPanel = new MemberPanel();
        if (JOptionPane.showConfirmDialog(null, memberPanel.mainPanel, "Tag hozzáadása", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = memberPanel.name.getText();
            String dob = memberPanel.dateOfBirth.getText();
            String phone = memberPanel.phone.getText();
            if (isValidInput(name, dob, phone)) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateOfBirth = LocalDate.parse(dob, dateFormatter);
                library.members.add(new Member(name, dateOfBirth, phone));
            }
        }
    }

    /**
     * Eltávolít egy könyvtári tagot a programból.
     *
     * @param member Az eltávolítandó {@code Member} objektum
     */
    public static void removeMember(Library library, Member member) {
        if (member == null)
            return;
        int chosenOption = JOptionPane.showConfirmDialog(null, "Biztosan törli a kiválasztott " +
                "tagot? Ha van kölcsönzött könyve, az törlődik.", "Biztosan törli?", JOptionPane.YES_NO_OPTION);
        if (chosenOption == JOptionPane.YES_OPTION) {
            List<Book> books = member.getBorrowedBooks();
            for (Book book : books)
                book.setBorrowedBy(null);
            library.members.remove(member);
        }
    }
}
