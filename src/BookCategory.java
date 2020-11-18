public enum BookCategory {
    novel,
    youth,
    scientific,
    textbook,
    dictionary,
    other;

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

    public static BookCategory valueOf(String localizedString, String fromLocale) {
        if (fromLocale.equals("HU")) {
            switch (localizedString) {
                case "regény":
                    return novel;
                case "ifjúsági":
                    return youth;
                case "tudományos":
                    return scientific;
                case "tankönyv":
                    return textbook;
                case "szótár":
                    return dictionary;
                case "egyéb":
                    return other;
            }
        }
        return other; // TODO
    }
}
