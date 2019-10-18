package dao;

import org.junit.*;
import dao.Database;
import dao.DatabaseException;
import dao.EventDAO;
import dao.PersonDAO;
import dao.model.Person;
import dao.model.Event;

import static org.junit.Assert.*;

import java.sql.ResultSet;

public class EventDAOTest {
    public EventDAOTest() {
    };
    
    @Before
    public void setUp() {
        edao = new EventDAO();
        
        // make a new person
        e = new Event();
        Person p = new Person("john", "Chris", "Colombus", "m");
        e.setPersonID(p.getPersonID());
        e.setLatitude(40);
        e.setLongitude(0);
        e.setCountry("england");
        e.setCity("greenwich");
        e.setEventType("exile");
        e.setYear(1492);
        e.setDescendant("bobby_thor");

        e2 = new Event();
        e2.setPersonID("p1");
        e2.setLatitude(-40);
        e2.setLongitude(0);
        e2.setCountry("africa");
        e2.setCity("greenwich");
        e2.setEventType("birth");
        e2.setYear(1450);
        e2.setDescendant("bobby_thor");

        e3 = new Event();
        e3.setPersonID("p2");
        e3.setLatitude(-130);
        e3.setLongitude(40);
        e3.setCountry("USA");
        e3.setCity("NYC");
        e3.setEventType("Whooped by Ninjas");
        e3.setYear(2100);
        e3.setDescendant("jackie_knight");
        PersonDAO pdao = new PersonDAO();

        try {
            // acquire the database before we open it
            Database.open();
            pdao.stash(p);
            edao.clear();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }
    }

    static EventDAO edao;
    static Event e;
    static Event e2;
    static Event e3;

    @After
    public void cleanUp() {
        try {
            // clear the person we had to make
            PersonDAO pdao = new PersonDAO();
            pdao.clear();
            Database.close();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }
    }
    
    /**
     * Testes that insertion works
     * @throws Exception
     */
    @Test
    public void stashPass() throws Exception {
        boolean success = false;
        
        try {
            edao.clear();
            success = edao.stash(e);
            // Database.commit();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }

        assertTrue(success);
    }

    /**
     * We try to stash the same person again:
     * this should violate the unique key constraint
     * @throws Exception
     */
    @Test(expected = DatabaseException.class)
    public void stashFail() throws Exception {
        edao.clear();
        // stash him twice
        edao.stash(e);
        edao.stash(e);
    }

    /**
     * we store person p, the look him up again and assert that we actually found something
     */
    @Test
    public void findPass() throws Exception {
        edao.clear();
        edao.stash(e);
        Event found = edao.find(e.getEventID());

        assertNotNull(found);
        assertEquals(e, found);
    }

    /**
     * we attempt to find a person in an empty database
     */
    @Test
    public void findFail() throws Exception {
        edao.clear();
        Event found = edao.find(e.getEventID());

        assertNull(found);
    }

    /**
     * we attempt to remove the person immediately after adding them
     */
    @Test
    public void removePass() throws Exception {
        edao.clear();
        edao.stash(e);
        int deleted = edao.remove(e.getEventID());

        assertEquals(1, deleted);
        assertNull(edao.find(e.getEventID()));
    }

    /**
     * we attempt to remove the person from the empty database
     */
    @Test
    public void removeFail() throws Exception {
        edao.clear();
        int deleted = edao.remove(e.getEventID());

        assertEquals(0, deleted);
    }

    /**
     * we add an event with a different userID, then remove all events with that
     * user ID. Be sure that event is not there, BUT the other still is
     * @throws Exception
     */
    @Test
    public void removeAllForUserPass() throws Exception {
        edao.clear();

        edao.stash(e);
        edao.stash(e2);
        edao.stash(e3);

        int deleted = edao.removeForUser("bobby_thor");

        assertTrue(deleted == 2);
        assertNotNull(edao.find(e3.getEventID()));
        assertNull(edao.find(e.getEventID()));
        assertNull(edao.find(e2.getEventID()));
    }

    /**
     * if we try to remove all events for a user, but they have none,
     * then we good.
     */
    @Test
    public void removeAllForUserPassEmpty() throws Exception {
        edao.clear();

        edao.stash(e3);

        int deleted = edao.removeForUser("bobby_thor");

        assertTrue(deleted == 0);
        assertNotNull(edao.find(e3.getEventID()));
    }
    
    /**
     * we should be able to fetch all the events for bobby_thor, and those only
     */
    @Test
    public void dumpPass() throws Exception {
        edao.clear();

        edao.stash(e);
        edao.stash(e2);
        edao.stash(e3);

        Event[] results = edao.dump("bobby_thor");

        assertNotNull(results);
        assertTrue(results.length == 2);
        assertTrue((results[0].equals(e) && results[1].equals(e2)) ||
                   (results[1].equals(e) && results[0].equals(e2)));
    }

    /**
     * we get nothing if the invoke a user who has no data in the DB
     * @throws Exception
     */
    @Test
    public void dumpPassEmpty() throws Exception {
        edao.clear();

        edao.stash(e);
        edao.stash(e2);
        edao.stash(e3);

        Event[] results = edao.dump("tommy_boy");

        assertTrue(results == null || results.length == 0);
    }

    /**
     * we can add many events at once: they all get added
     * @throws Exception
     */
    @Test
    public void stashManyPass() throws Exception {
        edao.clear();

        Event[] elist = new Event[3];
        elist[0] = e;
        elist[1] = e2;
        elist[2] = e3;

        assertTrue(edao.stashMany(elist));

        for (Event evnt : elist) {
            Event result = edao.find(evnt.getEventID());
            assertNotNull(result);
            assertEquals(result, evnt);
        }
    }

    /**
     * try adding a list which has a duplicate, this should fail
     */
    @Test(expected = DatabaseException.class)
    public void stashManyFail() throws Exception {
        edao.clear();

        Event[] elist = new Event[4];
        elist[0] = e;
        elist[1] = e2;
        elist[2] = e3;
        elist[3] = e2;

        assertFalse(edao.stashMany(elist));
    }

    /** 
     * clear should make there be nothing in the event table 
     */
    @Test
    public void clearPass() throws Exception {
        edao.clear();

        edao.stash(e);
        edao.stash(e2);

        edao.clear();

        assertNull(edao.find(e.getEventID()));
        assertNull(edao.find(e2.getEventID()));
    }
    
    /**
     * clearing multiple times in a row isn't a problem
     * @throws Exception
     */
    @Test
    public void clearPassEmpty() throws Exception {
        edao.clear();
        edao.clear();
        edao.clear();
    }
}