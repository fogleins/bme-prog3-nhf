import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A tagokat megjelenítő táblázat modellje.
 */
public class MemberData extends AbstractTableModel {
    /**
     * A tagok listája.
     */
    List<Member> members;

    /**
     * A tagokat tartalmazó {@code JComboBox} objektum.
     */
    JComboBox<Member> membersComboBox;

    /**
     * Konstruktor
     */
    public MemberData() {
        this.members = new ArrayList<>();
        this.membersComboBox = new JComboBox<>(); // TODO: initComboBox() biztosan nem kell?
    }

    /**
     * Konstruktor
     *
     * @param members A tagok listája, akiket meg szeretnénk jeleníteni a táblázatban
     */
    public MemberData(List<Member> members) {
        this.members = members;
        this.membersComboBox = new JComboBox<>();
        initComboBox();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public int getRowCount() {
        return members.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Member member = members.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return member.getName();
            case 1:
                return member.getDateOfBirth();
            case 2:
                return member.getPhone();
            default:
                return member.getMemberSince();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Név";
            case 1:
                return "Születési idő";
            case 2:
                return "Telefonszám";
            default:
                return "Tagság kezdete";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1 || columnIndex == 3)
            return LocalDate.class;
        return String.class;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            String name = (String) aValue;
            if (!name.equals(""))
                members.get(rowIndex).setName(name);
        } else if (columnIndex == 2) {
            String phone = (String) aValue;
            if (!phone.equals(""))
                members.get(rowIndex).setPhone(phone);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Hozzáad egy tagot a táblázathoz.
     *
     * @param member A hozzáadandó tag
     */
    public void add(Member member) {
        members.add(member);
        this.membersComboBox.addItem(member);
        fireTableDataChanged();
    }

    /**
     * Eltávolít egy tagot.
     *
     * @param member Az eltávolítandó tag
     */
    public void remove(Member member) {
        this.membersComboBox.removeItem(member);
        this.members.remove(member);
        fireTableDataChanged();
    }

    /**
     * Feltölti a ComboBox-ot a tagok adataival.
     */
    private void initComboBox() {
        for (Member member : this.members)
            this.membersComboBox.addItem(member);
    }

    /**
     * @return A tagok adatait tartalmazó ComboBox
     */
    public JComboBox<Member> getMembersComboBox() {
        return membersComboBox;
    }
}
