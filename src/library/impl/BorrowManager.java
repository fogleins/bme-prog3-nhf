package library.impl;

import javax.swing.*;

/**
 * A könyvek kölcsönözhetőségét és kölcsönzését kezelő osztály.
 */
public class BorrowManager {

    /**
     * Kezeli a könyv kölcsönözhetőségének megváltozását
     *
     * @param selectedBook A táblázatban kijelölt könyv, aminek a tulajdonsága megváltozik
     * @param aValue       Az új érték, amire át kell állítani
     */
    public static void borrowableChanged(Book selectedBook, Object aValue) {
        if (selectedBook.getBorrowedBy() != null) {
            int chosenOption = JOptionPane.showConfirmDialog(null, "A választott könyvet " +
                            "kikölcsönözték. Szeretné nem kölcsönözhetővé állítani? A hozzárendelt kölcsönző eltűnik.",
                    "A választott könyvet kikölcsönözték", JOptionPane.YES_NO_OPTION);
            if (chosenOption == JOptionPane.YES_OPTION) {
                selectedBook.getBorrowedBy().getBorrowedBooks().remove(selectedBook);
                selectedBook.setBorrowedBy(null);
            } else return;
        }
        selectedBook.setBorrowable((Boolean) aValue);
    }

    /**
     * Kezeli a könyvet kölcsönző személy megváltozását. Ha volt korábbi kölcsönző, akkor az ő kölcsönzött könyvei
     * közül eltávolítja, az új kölcsönző könyveihez pedig hozzáadja a könyvet.
     * Ha nem volt korábbi kölcsönző, csak hozzáadja a kölcsőnző könyveihez.
     *
     * @param selectedBook A táblázatban kijelölt könyv, amelyiknek a kölcsönzője megváltozik
     * @param aValue       Az új kölcsönző
     */
    public static void borrowedByChanged(Book selectedBook, Object aValue) {
        Member borrower = (Member) aValue;
        Member previousBorrower = selectedBook.getBorrowedBy();
        if (previousBorrower == borrower)
            return;
        if (borrower != null) {
            if (!borrower.getBorrowedBooks().contains(selectedBook))
                borrower.getBorrowedBooks().add(selectedBook);
        }
        if (previousBorrower != null)
            previousBorrower.getBorrowedBooks().remove(selectedBook);
        selectedBook.setBorrowedBy(borrower);
    }
}
