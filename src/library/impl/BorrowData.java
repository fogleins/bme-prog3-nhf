package library.impl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

/**
 * A kölcsönzéseket megjelenítő {@code JTable} modellje.
 */
public class BorrowData extends DefaultTreeModel {
    /**
     * A könyvtári tagok listája.
     */
    private final List<Member> members;

    /**
     * Konstruktor
     *
     * @param members A könyvtári tagok listája
     */
    public BorrowData(List<Member> members) {
        super(new DefaultMutableTreeNode(members));
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
        else /*if (members.contains(parent))*/ { // TODO
            Member member = ((Member) parent);
            Book book = member.getBorrowedBooks().get(index);
            return book.getAuthor() + ": " + book.getTitle();
        }
//        return null; // TODO
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == root)
            return this.members.size();
//        else if (members.contains(parent)) // TODO
        return ((Member) parent).getBorrowedBooks().size();
//        return 0; // todo
    }

    @Override
    public boolean isLeaf(Object node) {
        if (members.contains(node)) // ha Member
            return ((Member) node).getBorrowedBooks().size() == 0;
        else // ha root, akkor hamis, ha nem az (tehát könyv), akkor igaz
            return node != root;
    }
}
