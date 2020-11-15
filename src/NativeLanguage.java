public enum NativeLanguage {
    Hungarian,
    English,
    German,
    Spanish,
    Italian,
    French,
    other;

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
                    return Hungarian;
                case "angol":
                    return English;
                case "német":
                    return German;
                case "spanyol":
                    return Spanish;
                case "olasz":
                    return Italian;
                case "francia":
                    return French;
                default:
                    return other;
            }
        }
        return other; // TODO
    }
}
