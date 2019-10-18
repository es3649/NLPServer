package service;

import java.util.logging.Level;
import org.junit.*;

import dao.Database;
import dao.DatabaseException;
import dao.EventDAO;
import dao.model.Event;
import main.Server;
import service.ClearService;
import service.EventService;
import service.exception.ServiceErrorException;
import service.exception.ThatsNotYoursException;
import service.response.EventListResponse;
import service.response.EventResponse;
import service.response.Serializable;

import static org.junit.Assert.*;

/**
 * tests the EventService
 */
public class EventServiceTest {
    public EventServiceTest() {}

    private EventService elookuper;

    private static final Event e1;
    private static final String e1ID = "e1ID";
    private static final Event e2;
    private static final String e2ID = "e2ID";
    private static final Event e3;
    private static final String e3ID = "e3ID";
    private static final String USER1 = "USER1";
    private static final String USER2 = "USER2";

    static {
        e1 = new Event(e1ID);
        e1.setPersonID("p1");
        e1.setLatitude(40);
        e1.setLongitude(0);
        e1.setCountry("england");
        e1.setCity("greenwich");
        e1.setEventType("exile");
        e1.setYear(1492);
        e1.setDescendant(USER1);

        e2 = new Event(e2ID);
        e2.setPersonID("p1");
        e2.setLatitude(-40);
        e2.setLongitude(0);
        e2.setCountry("africa");
        e2.setCity("greenwich");
        e2.setEventType("birth");
        e2.setYear(1450);
        e2.setDescendant(USER1);

        e3 = new Event(e3ID);
        e3.setPersonID("p2");
        e3.setLatitude(-130);
        e3.setLongitude(40);
        e3.setCountry("USA");
        e3.setCity("NYC");
        e3.setEventType("Whooped by Ninjas");
        e3.setYear(2100);
        e3.setDescendant(USER2);
    }

    @Before
    public void setUp() {
        try {
            Database.open();
            EventDAO edao = new EventDAO();
            edao.stash(e1);
            edao.stash(e2);
            edao.stash(e3);
            Database.commit();
        } catch (DatabaseException ex) {
            Server.logger.log(Level.SEVERE, "Failed to add data for tests", ex);
        }
        Database.close();


        // make a new lookuper
        elookuper = new EventService();
    }

    @After
    public void cleanUp() {
        // clean up the lookuper
        elookuper = null;
        try {
            Database.open();
            EventDAO edao = new EventDAO();
            edao.clear();
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
        Serializable serial = elookuper.fetchEvent(USER1, e1ID);
        Event result = eventResponseToEvent((EventResponse)serial);

        assertNotNull(result);
        assertEquals(result, e1);

        // test e2
        serial = elookuper.fetchEvent(USER1, e2ID);
        result = eventResponseToEvent((EventResponse)serial);

        assertNotNull(result);
        assertEquals(result, e2);

        // test e3
        serial = elookuper.fetchEvent(USER2, e3ID);
        result = eventResponseToEvent((EventResponse)serial);

        assertNotNull(result);
        assertEquals(result, e3);
    }

    private Event eventResponseToEvent(EventResponse response) {
        Event e = new Event(response.getEventID());
        e.setCity(response.getCity());
        e.setCountry(response.getCountry());
        e.setDescendant(response.getDescendant());
        e.setYear(response.getYear());
        e.setLongitude(response.getLongitude());
        e.setLatitude(response.getLatitude());
        e.setPersonID(response.getPersonID());
        e.setEventType(response.getEventType());
        return e;
    }
    
        /**
         * tests that permissions are enforced when looking up events
         * with the wrong credentials
         * @throws Exception
         */
        @Test(expected = ThatsNotYoursException.class)
        public void singleLookupFail_NoPermission() throws Exception {
            Serializable result = elookuper.fetchEvent(USER2, e1ID);
        }

    /**
     * verifies that we can query all events for a single user
     * @throws Exception if something goes wrong
     */
    @Test
    public void multipleLookupPass() throws Exception {
        Serializable serial = elookuper.fetchEventAll(USER1);
        EventListResponse response = (EventListResponse)serial;

        Event[] results = response.getEventList();

        // there should be exactly 2 events returned
        assertTrue(results.length == 2);

        // none of them should be e3
        for (Event e : results) {
            assertNotNull(e);
            assertNotEquals(e, e3);
        }

        // make sure we got exacelt e1 and e2
        assertTrue(
            (results[0].equals(e1) && results[1].equals(e2)) ||
            (results[0].equals(e2) && results[1].equals(e1)));
    }

    /**
     * try looking up all events when you don't have any
     * @throws Exception
     */
    @Test
    public void multipleLookupPass_empty() throws Exception {
        Serializable serial = elookuper.fetchEventAll("USER3");
        EventListResponse response = (EventListResponse)serial;

        Event[] results = response.getEventList();

        assertNotNull(results);
        assertTrue(results.length == 0);
    }
}