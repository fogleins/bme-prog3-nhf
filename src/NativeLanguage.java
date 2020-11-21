public enum NativeLanguage {
    HUNGARIAN,
    ENGLISH,
    GERMAN,
    SPANISH,
    ITALIAN,
    FRENCH,
    OTHER;

    public String getLocalizedName(String locale) {
        if (locale.equals("HU")) {
            String[] languagesHU = { "magyar", "angol", "német", "spanyol", "olasz", "francia", "egyéb" };
            return languagesHU[this.ordinal()];
        }
        return null;
    }

    public String getLocalizedName() {
        return getLocalizedName("HU");
    }

    public static NativeLanguage valueOf(String localizedString, String fromLocale) {
        if (fromLocale.equals("HU")) {
            switch (localizedString) {
                case "magyar":
                    return HUNGARIAN;
                case "angol":
                    return ENGLISH;
                case "német":
                    return GERMAN;
                case "spanyol":
                    return SPANISH;
                case "olasz":
                    return ITALIAN;
                case "francia":
                    return FRENCH;
                case "egyéb":
                    return OTHER;
                default:
                    return null;
            }
        }
        return null;
    }
}
