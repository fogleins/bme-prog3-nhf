import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Az összes tárolt adat sorosításáért felelős osztály */
public class Library implements Serializable {
    List<Book> books = new ArrayList<>();
    List<Author> authors = new ArrayList<>();
    List<Member> members = new ArrayList<>();
}
