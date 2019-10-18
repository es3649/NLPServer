package service;

import org.junit.*;

import dao.Database;
import dao.EventDAO;
import dao.PersonDAO;
import dao.UserDAO;
import dao.model.Event;
import dao.model.Person;
import dao.model.User;
import service.exception.BadRequestException;
import service.exception.ServiceErrorException;

import static org.junit.Assert.*;

/**
 * tests the FillService
 */
public class FillServiceTest {
    public FillServiceTest() {};

    private static User u1;
    private static User u2;
    private static Person p1;
    private static Person p2;
    private static Person p1mom;
    private static Event p1Event;
    private static final String USER1 = "USER1";
    private static final String USER2 = "USER2";
    private FillService filler;

    @BeforeClass
    public static void setUpAll() {
        u1 = new User(USER1, "Password", "Email", "bill", "gates", "m");
        u2 = new User(USER2, "pwdsecrets", "no-reply@afer.com", "ben", "gated", "m");
        p1 = u1.MakePerson();
        p2 = u2.MakePerson();
        p1mom = new Person(USER1, "gemma", "fitz", "f");
        p1.setMotherID(p1mom.getPersonID());

        p1Event = new Event(USER2);
        p1Event.setPersonID(p1.getPersonID());
        p1Event.setCity("city");
        p1Event.setCountry("country");
        p1Event.setEventType("eventType");
        p1Event.setDescendant(USER1);

        ClearService clearer = new ClearService();
        try {
            clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("Oops, left a mess!");
        }
    }

    @Before
    public void setUp() {
        filler = new FillService();
    }

    @After
    public void cleanUp() {
        filler = null;
        ClearService clearer = new ClearService();
        try {
            clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("Oops, left a mess!");
        }
    }

    /**
     * fill should fail when we get an unknown username
     */
    @Test(expected = BadRequestException.class)
    public void fillTestFail_badUsername() throws Exception {
        UserDAO udao = new UserDAO();
        PersonDAO pdao = new PersonDAO();

        try {
            Database.open();
            pdao.stash(p1);
            
            udao.stash(u1);
            Database.commit();
        } finally {
            Database.close();
        }

        // this should throw an exception, USER2 is unknown
        filler.fill(USER2, 3);
    }

    /**
     * be sure that old data is overwritten
     * @throws Exception if crap happens or if we fail
     */
    @Test
    public void fillPass_overwriteData() throws Exception {
        PersonDAO pdao = new PersonDAO();
        UserDAO udao = new UserDAO();
        EventDAO edao = new EventDAO();
        try {
            Database.open();
            pdao.stash(p1);
            pdao.stash(p2);
            pdao.stash(p1mom);
            
            udao.stash(u1);
            udao.stash(u2);
            Database.commit(); 
        } finally {
            Database.close();
        }

        filler.fill(USER1, 3);

        try {
            Database.open();

            // p1 should not be gone, p2 should still be around,
            // but p1mom should not, same with the event for p1
            assertNotNull(pdao.find(p1.getPersonID()));
            assertNotNull(pdao.find(p2.getPersonID()));
            assertNull(pdao.find(p1mom.getPersonID()));

            assertNull(edao.find(p1Event.getEventID()));
        } finally {
            Database.close();
        }

    }
}