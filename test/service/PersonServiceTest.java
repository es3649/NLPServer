package service;

import java.util.logging.Level;
import org.junit.*;

import dao.Database;
import dao.DatabaseException;
import dao.PersonDAO;
import dao.model.Person;
import main.Server;
import service.PersonService;
import service.exception.ServiceErrorException;
import service.exception.ThatsNotYoursException;
import service.response.PersonListResponse;
import service.response.PersonResponse;
import service.response.Serializable;

import static org.junit.Assert.*;

/**
 * tests the PersonService
 */
public class PersonServiceTest {
    public PersonServiceTest() {}

    private PersonService plookuper;
    

    private static final Person p1;
    private static final String p1ID = "p1ID";
    private static final Person p2;
    private static final String p2ID = "p2ID";
    private static final Person p3;
    private static final String p3ID = "p3ID";
    private static final String USER1 = "USER1";
    private static final String USER2 = "USER2";

    static {
        p1 = new Person(p1ID);
        p1.setGivenName("michael");
        p1.setSurname("coulsen");
        p1.setGender("m");
        p1.setFatherID(p1ID);
        p1.setMotherID(p2ID);
        p1.setSpouseID(p3ID);
        p1.setPertinentUser(USER1);

        p2 = new Person(p2ID);
        p2.setGivenName("tammy");
        p2.setSurname("phillip");
        p2.setGender("f");
        p2.setFatherID(p1ID);
        p2.setMotherID(p2ID);
        p2.setSpouseID(p3ID);
        p2.setPertinentUser(USER1);

        p3 = new Person(p3ID);
        p3.setGivenName("james");
        p3.setSurname("daff");
        p3.setGender("m");
        p3.setFatherID(p1ID);
        p3.setMotherID(p2ID);
        p3.setSpouseID(p3ID);
        p3.setPertinentUser(USER2);
    }

    @Before
    public void setUp() {
        try {
            Database.open();
            PersonDAO pdao = new PersonDAO();
            pdao.stash(p1);
            pdao.stash(p2);
            pdao.stash(p3);
            Database.commit();
        } catch (DatabaseException ex) {
            Server.logger.log(Level.SEVERE, "Failed to add data for tests", ex);
        }
        Database.close();


        // make a new lookuper
        plookuper = new PersonService();
    }

    @After
    public void cleanUp() {
        // clean up the lookuper
        plookuper = null;
        try {
            Database.open();
            PersonDAO pdao = new PersonDAO();
            pdao.clear();
        } catch (DatabaseException ex) {
            Server.logger.log(Level.SEVERE, "Failed to clean up after test", ex);
        }
        Database.close();
    }

    @BeforeClass
    public static void setUpAll() {
        try {
            ClearService clearer = new ClearService();
            clearer.Clear();
        } catch (ServiceErrorException ex) {
            Server.logger.log(Level.SEVERE, "Failed to clear database in preparation for tests", ex);
        }
    }

    /**
     * tests that we can successfully do a single object lookup
     * @throws Exception ig there is some kind of error
     */
    @Test
    public void singleLookupPass() throws Exception {
        // test e1
        Serializable serial = plookuper.fetchPerson(USER1, p1ID);
        Person result = personResponseToPerson((PersonResponse)serial);

        assertNotNull(result);
        assertEquals(result, p1);

        // test e2
        serial = plookuper.fetchPerson(USER1, p2ID);
        result = personResponseToPerson((PersonResponse)serial);

        assertNotNull(result);
        assertEquals(result, p2);

        // test e3
        serial = plookuper.fetchPerson(USER2, p3ID);
        result = personResponseToPerson((PersonResponse)serial);

        assertNotNull(result);
        assertEquals(result, p3);
    }

    private Person personResponseToPerson(PersonResponse response) {
        Person p = new Person(response.getPersonID());
        p.setPertinentUser(response.getPertinentUser());
        p.setGivenName(response.getGivenName());
        p.setSurname(response.getSurname());
        p.setGender(response.getGender());
        p.setFatherID(response.getFatherID());
        p.setMotherID(response.getMotherID());
        p.setSpouseID(response.getSpouseID());
        return p;
    }
    
        /**
         * tests that permissions are enforced when looking up persons
         * with the wrong credentials
         * @throws Exception
         */
        @Test(expected = ThatsNotYoursException.class)
        public void singleLookupFail_NoPermission() throws Exception {
            Serializable result = plookuper.fetchPerson(USER2, p1ID);
        }

    /**
     * verifies that we can query all persons for a single user
     * @throws Exception if something goes wrong
     */
    @Test
    public void multipleLookupPass() throws Exception {
        Serializable serial = plookuper.fetchPersonAll(USER1);
        PersonListResponse response = (PersonListResponse)serial;

        Person[] results = response.getPersons();

        // there should be exactly 2 persons returned
        assertTrue(results.length == 2);

        // none of them should be e3
        for (Person p : results) {
            assertNotNull(p);
            assertNotEquals(p, p3);
        }

        // make sure we got exacelt e1 and e2
        assertTrue(
            (results[0].equals(p1) && results[1].equals(p2)) ||
            (results[0].equals(p2) && results[1].equals(p1)));
    }

    /**
     * try looking up all events when you don't have any
     * @throws Exception
     */
    @Test
    public void multipleLookupPass_empty() throws Exception {
        Serializable serial = plookuper.fetchPersonAll("USER3");
        PersonListResponse response = (PersonListResponse)serial;

        Person[] results = response.getPersons();

        assertNotNull(results);
        assertTrue(results.length == 0);
    }
}