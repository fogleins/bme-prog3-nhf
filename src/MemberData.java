import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
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
                return member.getDateOfBirth();
            default:
                return member.getPhone();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0: return "Név";
            case 1: return "Születési idő";
            default: return "Telefonszám";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1)
            return LocalDate.class;
        return String.class;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            String name = (String) aValue;
            if (!name.equals(""))
                members.get(rowIndex).setName(name);
        }
        else if (columnIndex == 2) {
            String phone = (String) aValue;
            if (!phone.equals(""))
                members.get(rowIndex).setPhone(phone);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 1;
    }

    private boolean listContains(Member member) {
        for (Member m: members) {
            if (m.getName().equals(member.getName()) && m.getDateOfBirth().isEqual(member.getDateOfBirth()) && m.getPhone().equals(member.getPhone()))
                return true;
        }
        return false;
    }

    public void addMember(Member member) throws MissingRequiredArgumentException, PersonAlreadyAddedException {
        if (member.getName().equals("") || member.getPhone().equals("06301234567"))
            throw new MissingRequiredArgumentException();
        if (listContains(member))
            throw new PersonAlreadyAddedException(member + " már szerepel a programban, így nem lesz hozzáadva.");
        members.add(member);
        this.membersComboBox.addItem(member);
        fireTableDataChanged();
    }

    public void addMember(String name, LocalDate dateOfBirth, String phone) throws MissingRequiredArgumentException, PersonAlreadyAddedException { // TODO: JOptionPane
        addMember(new Member(name, dateOfBirth, phone));
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
