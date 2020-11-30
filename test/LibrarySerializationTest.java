import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LibrarySerializationTest {
    Library library;

    @Before
    public void init() {
        this.library = new Library();
    }

    @Test
    public void read() {
        this.library.initTransientVariables("serializationTestRead.libdat");
        this.library = Library.readDataFromFile(library);
        Assert.assertEquals(1, library.getBooks().size());
        Assert.assertEquals("Harry Potter és a Titkok Kamrája", library.getBooks().get(0).getTitle());
    }

    @Test
    public void write() {
        this.library.initTransientVariables("serializationTestWrite.libdat");
        this.library.addMember("Kiss Ferenc", "2000-02-02", "06301234567");
        this.library.addBook("J. K. Rowling", "Harry Potter", "2000", BookCategory.YOUTH, "magyar", true);
        this.library.saveData();

        // ellenőrzés céljából visszaolvassuk, és megnézzük, hogy visszakaptuk-e a fent megadott értékeket
        this.library = new Library();
        this.library.initTransientVariables("serializationTestWrite.libdat");
        this.library = Library.readDataFromFile(this.library);
        Assert.assertEquals("Kiss Ferenc", library.getMembers().get(0).getName());
        Assert.assertEquals("Harry Potter", library.getBooks().get(0).getTitle());
    }
}
