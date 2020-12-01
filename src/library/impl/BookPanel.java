package library.impl;

import javax.swing.*;
import java.awt.*;

/**
 * Létrehoz egy könyv adatainak megadását lehetővé tevő panelt a szükséges komponensekkel és lehetővé teszi a komponensek értékeinek későbbi elérését.
 */
class BookPanel extends JPanel {
    /**
     * Az adatok megadásának panele.
     */
    private final JPanel mainPanel;

    /**
     * A könyv szerzőjének megadására szolgáló szövegmező.
     */
    private final JTextField author;

    /**
     * A könyv címének szövegmezője.
     */
    private final JTextField title;

    /**
     * A könyv kiadási évének szövegmezője.
     */
    private final JTextField year;

    /**
     * A könyv kategóriájának megadását lehetővé tevő legördülő menü.
     */
    private final JComboBox<String> category;

    /**
     * A könyv nyelvének szövegmezője.
     */
    private final JTextField language;

    /**
     * A kölcsönözhetőség igaz értékét képviselő RadioButton.
     */
    private final JRadioButton yes;


    /**
     * Létrehozza a könyvek adatainak megadását lehetővé tevő panelt.
     */
    BookPanel() {
        this.mainPanel = new JPanel(new BorderLayout(5, 5)); // a JOptionPane fő panele

        // Labelek létrehozása és panelhez adása
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2)); // a JLabeleket tartalmazó panel
        label.add(new JLabel("Szerző", SwingConstants.RIGHT));
        label.add(new JLabel("Cím", SwingConstants.RIGHT));
        label.add(new JLabel("Kiadás éve", SwingConstants.RIGHT));
        label.add(new JLabel("Kategória", SwingConstants.RIGHT));
        label.add(new JLabel("Nyelv", SwingConstants.RIGHT));
        label.add(new JLabel("Kölcsönözhető?", SwingConstants.RIGHT));
        mainPanel.add(label, BorderLayout.WEST);

        // a felhasználó által szerkeszthető komponensek létrehozása és panelhez adása
        JPanel input = new JPanel(new GridLayout(0, 1, 2, 2));
        this.author = new JTextField(20);
        this.title = new JTextField(20);
        this.year = new JTextField(20);
        this.category = new JComboBox<>();
        // a ComboBox feltöltése a lehetséges értékekkel
        for (BookCategory cat : BookCategory.values())
            category.addItem(cat.getLocalizedName());
        this.language = new JTextField("magyar", 20);
        // kölcsönözhetőség megadásának komponensei
        ButtonGroup borrowableButtons = new ButtonGroup();
        this.yes = new JRadioButton("Igen");
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
        mainPanel.add(input, BorderLayout.CENTER);
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
     * Visszaadja a szerző szövegmezőben megadott értéket.
     *
     * @return A szerző szövegmező értéke
     */
    public String getAuthor() {
        return author.getText();
    }

    /**
     * Visszaadja a cím szövegmezőben megadott értéket.
     *
     * @return A cím szövegmező értéke
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * Visszaadja az év szövegmezőben megadott értéket.
     *
     * @return A kiadási év szövegmező értéke
     */
    public String getYear() {
        return year.getText();
    }

    /**
     * Visszaadja a kategória ComboBox-ban kiválasztott értéket.
     *
     * @return A kiválasztott kategória
     */
    public String getCategory() {
        return (String) category.getSelectedItem();
    }

    /**
     * Visszaadja a könyv nyelvének szövegmezőjében megadott értéket.
     *
     * @return A könyv nyelve
     */
    public String getLanguage() {
        return language.getText();
    }

    /**
     * Visszaadja, hogy a könyvet kölcsönözhetőre állították-e.
     *
     * @return A könyv kölcsönözhetősége
     */
    public boolean isBorrowable() {
        return yes.isSelected();
    }
}
