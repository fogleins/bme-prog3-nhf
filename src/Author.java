import java.io.Serializable;

public class Author implements Serializable {

    private String name;
    private int birthyear;
    public NativeLanguage language;

    /** A szerző osztály konstruktora
     *
     * @param name A szerző neve
     * @param birthyear A szerző születési éve
     * @param language A szerző anyanyelve
     */
    public Author(String name, int birthyear, NativeLanguage language) {
        this.name = name;
        this.birthyear = birthyear;
        this.language = language;
    }

    /** Visszaadja a szerző nevét
     *
     * @return A szerző neve
     */
    public String getName() {
        return name;
    }

    /** Beállítja a szerző nevét
     *
     * @param name A szerző neve
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Visszaadja a szerző születési évét
     *
     * @return A szerző születési éve
     */
    public int getBirthyear() {
        return birthyear;
    }

    /** Beállítja a szerző születési évét
     *
     * @param birthyear A szerző születési éve
     */
    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }

    /** Visszaadja a szerző anyanyelvét
     *
     * @return A szerző anyanyelve
     */
    public NativeLanguage getNativeLanguage() {
        return language;
    }

    public void setNativeLanguage(NativeLanguage language) {
        this.language = language;
    }


    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", birthyear=" + birthyear +
                ", language=" + language +
                '}';
    }
}
