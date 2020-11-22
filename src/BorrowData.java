import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class BorrowData extends DefaultTreeModel {
    private List<Member> members;

    public BorrowData(DefaultMutableTreeNode root, List<Member> members) {
        super(root);
        this.members = members;
    }

    @Override
    public Object getRoot() {
        return this.root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == root)
            return this.members.get(index);
        else if (members.contains(parent)) {
            Member member = ((Member) parent);
            return member.getBorrowedBooks().get(index).getTitle();
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == root)
            return this.members.size();
        else if (members.contains(parent))
            return ((Member) parent).getBorrowedBooks().size();
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        if (members.contains(node)) // ha Member
            return ((Member) node).getBorrowedBooks().size() == 0;
        else // ha root, akkor hamis, ha nem az (tehát könyv), akkor igaz
            return node != root;
    }
}
