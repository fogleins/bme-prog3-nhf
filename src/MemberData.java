import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;

public class MemberData extends AbstractTableModel {
    List<Member> members = new ArrayList<>();
    JComboBox<Member> membersComboBox = new JComboBox<>();

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

    public void addMember(String name, int birthyear, String phone) {
        Member member = new Member(name, birthyear, phone);
        members.add(member);
        this.membersComboBox.addItem(member);
        fireTableDataChanged();
    }

    public void initComboBoxList() {
        for (Member member : this.members)
            this.membersComboBox.addItem(member);
    }

    public JComboBox<Member> getMembersComboBox() {
        return membersComboBox;
    }
}
