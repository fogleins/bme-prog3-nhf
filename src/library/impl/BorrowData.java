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

    /**
     * Visszaadja a fa gyökerét.
     *
     * @return A fa gyökere
     */
    @Override
    public Object getRoot() {
        return this.root;
    }

    /**
     * Visszaadja egy adott csomópont gyerekét.
     *
     * @param parent A szülő
     * @param index  A szülő keresett gyerekének az indexe
     * @return A paraméterként kapott szülő gyereke
     */
    @Override
    public Object getChild(Object parent, int index) {
        if (parent == root)
            return this.members.get(index);
        else {
            Member member = ((Member) parent);
            Book book = member.getBorrowedBooks().get(index);
            return book.getAuthor() + ": " + book.getTitle();
        }
    }

    /**
     * Visszaadja a szülő gyerekeinek számát.
     *
     * @param parent A szülő
     * @return A gyerekek száma
     */
    @Override
    public int getChildCount(Object parent) {
        if (parent == root)
            return this.members.size();
        return ((Member) parent).getBorrowedBooks().size();
    }

    /**
     * Visszaadja, hogy egy adott csomópont levél-e.
     *
     * @param node A csomópont, amiről meg szeretnénk tudni, hogy levél-e
     * @return Igaz, ha a csomópont levél, egyébként hamis
     */
    @Override
    public boolean isLeaf(Object node) {
        if (members.contains(node))
            return ((Member) node).getBorrowedBooks().size() == 0;
        else // ha root, akkor hamis, ha nem az (tehát könyv), akkor igaz
            return node != root;
    }
}
