package library.impl;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;

/**
 * Létrehoz egy tagok adatai megadását lehetővé tevő panelt a szükséges komponensekkel és lehetővé teszi a komponensek későbbi elérését és szerkesztését.
 */
class MemberPanel extends JPanel {
    /**
     * Az adatok megadásának panele.
     */
    private JPanel mainPanel;

    /**
     * A tag nevének szövegmezője.
     */
    private JTextField name;

    /**
     * A tag születési idejének szövegmezője.
     */
    private JFormattedTextField dateOfBirth;

    /**
     * A tag telefonszámának szövegmezője.
     */
    private JTextField phone;

    /**
     * Létrehoz egy tagok adatainak megadását lehetővé tevő panelt és inicializálja a komponenseit.
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
        this.name = new JTextField(10);
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
        this.phone = new JTextField(10);

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

    /**
     * Visszaadja a főpanelt.
     *
     * @return A főpanel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Visszaadja a név szövegmezőben megadott értéket.
     *
     * @return A név megadására szolgáló TextField tartalma
     */
    public String getNameValue() {
        return name.getText();
    }

    /**
     * Beállítja a név megadására való TextField értékét.
     *
     * @param name A név, amire állítani szeretnénk a TextField-et
     */
    public void setNameValue(String name) {
        this.name.setText(name);
    }

    /**
     * Visszaadja a születési idő mezőben megadott értéket.
     *
     * @return A születési év TextField értéke
     */
    public String getDateOfBirthValue() {
        return dateOfBirth.getText();
    }

    /**
     * Beállítja a születési év TextField értékét.
     *
     * @param dateOfBirth Az érték, amire állítani szeretnénk
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth.setText(dateOfBirth);
    }

    /**
     * Visszaadja a telefonszám szövegmezőben megadott értéket.
     *
     * @return A telefonszám TextField értéke
     */
    public String getPhoneValue() {
        return phone.getText();
    }

    /**
     * A telefonszám megadásárára szolgáló TextField értékének beállítása.
     *
     * @param phone A telefonszám, amire állítani szeretnénk
     */
    public void setPhone(String phone) {
        this.phone.setText(phone);
    }
}