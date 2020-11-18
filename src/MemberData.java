import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MemberData extends AbstractTableModel {
    List<Member> members;
    JComboBox<Member> membersComboBox;

    public MemberData() {
        this.members = new ArrayList<>();
        this.membersComboBox = new JComboBox<>(); // TODO: initComboBox() biztosan nem kell?
    }

    public MemberData(List<Member> members) {
        this.members = members;
        this.membersComboBox = new JComboBox<>();
        initComboBox();
    }

    @Override
    public int getColumnCount() {
        return 3;
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
                return member.getBirthyear();
            default:
                return member.getPhone();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Név";
            case 1: return "Születési év";
            default: return "Telefonszám";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1)
            return Integer.class;
        return String.class;
    }

    private boolean listContains(Member member) {
        for (Member m: members) {
            if (m.getName().equals(member.getName()) && m.getBirthyear() == member.getBirthyear() && m.getPhone().equals(member.getPhone()))
                return true;
        }
        return false;
    }

    public void addMember(String name, int birthyear, String phone) { // TODO: JOptionPane
        Member member = new Member(name, birthyear, phone);
        if (!listContains(member)) {
            members.add(member);
            this.membersComboBox.addItem(member);
            fireTableDataChanged();
        }
    }

    private void initComboBox() {
        this.membersComboBox = new JComboBox<>();
        for (Member member : this.members)
            this.membersComboBox.addItem(member);
    }

    public JComboBox<Member> getMembersComboBox() {
        return membersComboBox;
    }
}
