public enum BookCategory {
    NOVEL,
    YOUTH,
    SCIENTIFIC,
    TEXTBOOK,
    DICTIONARY,
    OTHER;

    public String getLocalizedName(String locale) {
        if (locale.equals("HU")) {
            String[] categoriesHU = { "regény", "ifjúsági", "tudományos", "tankönyv", "szótár", "egyéb" };
            return categoriesHU[this.ordinal()];
        }
        return this.name();
    }

    public String getLocalizedName() {
        return getLocalizedName("HU");
    }

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
