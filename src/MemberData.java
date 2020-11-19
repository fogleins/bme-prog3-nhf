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

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            String name = (String) aValue;
            if (!name.equals(""))
                members.get(rowIndex).setName(name);
        }
        else if (columnIndex == 1) {
            members.get(rowIndex).setBirthyear((Integer) aValue);
        }
        else if (columnIndex == 2) {
            String phone = (String) aValue;
            if (!phone.equals(""))
                members.get(rowIndex).setPhone(phone);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    private boolean listContains(Member member) {
        for (Member m: members) {
            if (m.getName().equals(member.getName()) && m.getBirthyear() == member.getBirthyear() && m.getPhone().equals(member.getPhone()))
                return true;
        }
        return false;
    }

    public void addMember(Member member) throws MissingRequiredArgumentException {
        if (member.getName().equals("") || member.getBirthyear() == 0 || member.getPhone().equals(""))
            throw new MissingRequiredArgumentException();
        if (!listContains(member)) {
            members.add(member);
            this.membersComboBox.addItem(member);
            fireTableDataChanged();
        }
    }

    public void addMember(String name, int birthyear, String phone) throws MissingRequiredArgumentException { // TODO: JOptionPane
        addMember(new Member(name, birthyear, phone));
    }

    public void removeMember(Member member) {
        this.members.remove(member);
        fireTableDataChanged();
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
