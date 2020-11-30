package library.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Ellenőrzi, hogy egy adott input megfelel-e az objektum létrehozásához szükséges formátumoknak.
 */
public class ValidityChecker {
    /**
     * Ellenőrzi, hogy a paraméterül kapott adatok lehetnek-e egy könyv adatai.
     *
     * @param author   A könyv szerzője
     * @param title    A könyv címe
     * @param year     A könyv kiadási éve
     * @param language A könyv nyelve
     * @return Hamis, ha legalább egy paraméter nem felel meg az elvárt formátumnak, egyébként igaz
     */
    public static boolean isValidBookInput(String author, String title, String year, String language) {
        if (author.equals("") || title.equals("") || language.equals(""))
            return false;
        try {
            Integer.parseInt(year);
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }

    /**
     * Megvizsgálja, hogy a paraméterül kapott adatok lehetnek-e egy {@code Member} adatai.
     *
     * @param name  A felhasználó által megadott név
     * @param dob   A felhasználó által megadott születési idő
     * @param phone A felhasználó által megadott telefonszám
     * @return Hamis, ha valamelyik paraméter nem felel meg az elvárt formátumnak, egyébként igaz
     */
    public static boolean isValidMemberInput(String name, String dob, String phone) {
        if (name.equals("") || !(phone.matches("\\d+") || phone.equals("")))
            return false;
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dob, dateFormatter);
        } catch (DateTimeParseException parseException) {
            return false;
        }
        return true;
    }
}
