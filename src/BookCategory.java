/**
 * Egy könyv lehetséges kategóriái.
 */
public enum BookCategory {
    NOVEL,
    YOUTH,
    SCIENTIFIC,
    TEXTBOOK,
    DICTIONARY,
    OTHER;

    /**
     * Visszaadja a kategória magyar nevét {@code String}-ként
     *
     * @param locale A nyelv ISO 639-1 kódja, csupa nagybetűvel
     * @return A kategória neve a megadott nyelven, ha az adott nyelven elérhető. Ha nem, visszaadja az angol nevét.
     */
    public String getLocalizedName(String locale) {
        if (locale.equals("HU")) {
            String[] categoriesHU = {"regény", "ifjúsági", "tudományos", "tankönyv", "szótár", "egyéb"};
            return categoriesHU[this.ordinal()];
        }
        return this.name();
    }

    /**
     * @return A kategória magyar neve
     */
    public String getLocalizedName() {
        return getLocalizedName("HU");
    }

    /**
     * Visszaadja a lokalizált stringhez tartozó értéket.
     * @param localizedName A string lokalizált értéke
     * @param fromLocale A forrásnyelv ISO 639-1 kódja, csupa nagybetűvel
     * @return A paraméterül kapott {@code String}-hez tartozó {@code BookCategory} érték. Ha a megadott nyelvre a függvény
     * nincs implementálva, a függvény {@code null} értékkel tér vissza.
     */
    public static BookCategory valueOf(String localizedName, String fromLocale) {
        if (fromLocale.equals("HU")) {
            switch (localizedName) {
                case "regény":
                    return NOVEL;
                case "ifjúsági":
                    return YOUTH;
                case "tudományos":
                    return SCIENTIFIC;
                case "tankönyv":
                    return TEXTBOOK;
                case "szótár":
                    return DICTIONARY;
                case "egyéb":
                    return OTHER;
                default:
                    return null;
            }
        }
        return null;
    }
}
