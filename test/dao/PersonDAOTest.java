package dao;

import org.junit.*;
import dao.Database;
import dao.DatabaseException;
import dao.PersonDAO;
import dao.model.Person;

import static org.junit.Assert.*;

import java.sql.ResultSet;

public class PersonDAOTest {
    public PersonDAOTest() {
    };
    
    @BeforeClass
    public static void setUp() {
        pdao = new PersonDAO();
        
        // make a new person
        p = new Person("example@example.com", "ike", "Stonewhall", "m");
        p2 = new Person("user", "firstName", "lastName", "f");
        p3 = new Person("user", "real_name", "words", "m");

        try {
            // acquire the database before we open it
            Database.open();
            pdao.clear();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }

    }

    static PersonDAO pdao;
    static Person p;
    static Person p2;
    static Person p3;

    @AfterClass
    public static void cleanUp() {
        Database.close();
    }
    
    /**
     * Testes that insertion works
     * @throws Exception
     */
    @Test
    public void stashPass() throws Exception {
        boolean success = false;
        
        try {
            pdao.clear();
            success = pdao.stash(p);
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
        pdao.clear();
        // stash him twice
        pdao.stash(p);
        pdao.stash(p);
    }

    /**
     * we store person p, the look him up again and assert that we actually found something
     */
    @Test
    public void findPass() throws Exception {
        pdao.clear();
        pdao.stash(p);
        Person found = pdao.find(p.getPersonID());

        assertNotNull(found);
        assertEquals(p, found);
    }

    /**
     * we attempt to find a person in an empty database
     */
    @Test
    public void findFail() throws Exception {
        pdao.clear();
        Person found = pdao.find(p.getPersonID());

        assertNull(found);
    }

    /**
     * we attempt to remove the person immediately after adding them
     */
    @Test
    public void removePass() throws Exception {
        pdao.clear();
        pdao.stash(p);
        int deleted = pdao.remove(p.getPersonID());

        assertEquals(1, deleted);
        assertNull(pdao.find(p.getPersonID()));
    }

    /**
     * we attempt to remove the person from the empty database
     */
    @Test
    public void removeFail() throws Exception {
        pdao.clear();
        int deleted = pdao.remove(p.getPersonID());

        assertEquals(0, deleted);
    }

    /**
     * we add a person with a different userID, then remove all persons with that
     * user ID. Be sure that person is not there, BUT the other still is
     * @throws Exception
     */
    @Test
    public void removeAllForUserPass() throws Exception {
        pdao.clear();

        pdao.stash(p);
        pdao.stash(p2);

        int deleted = pdao.removeForUser("user");

        assertTrue(deleted == 1);
        assertNull(pdao.find(p2.getPersonID()));
        assertNotNull(pdao.find(p.getPersonID()));
    }

    /**
     * if we try to remove all events for a user, but they have none,
     * then we good.
     */
    @Test
    public void removeAllForUserPassEmpty() throws Exception {
        pdao.clear();

        pdao.stash(p);

        int deleted = pdao.removeForUser("user");

        assertTrue(deleted == 0);
        assertNotNull(pdao.find(p.getPersonID()));
    }
    
    /**
     * we should be able to fetch all the events for bobby_thor, and those only
     */
    @Test
    public void dumpPass() throws Exception {
        pdao.clear();

        pdao.stash(p);
        pdao.stash(p2);
        pdao.stash(p3);

        Person[] results = pdao.dump("user");

        assertNotNull(results);
        assertTrue(results.length == 2);
        assertTrue((results[0].equals(p2) && results[1].equals(p3)) ||
                   (results[1].equals(p2) && results[0].equals(p3)));
    }

    /**
     * we get nothing if the invoke a user who has no data in the DB
     * @throws Exception
     */
    @Test
    public void dumpPassEmpty() throws Exception {
        pdao.clear();

        pdao.stash(p);
        pdao.stash(p2);
        pdao.stash(p3);

        Person[] results = pdao.dump("tommy_boy");

        assertTrue(results == null || results.length == 0);
    }

    /**
     * we can add many peple at once: they all get added
     * @throws Exception
     */
    @Test
    public void stashManyPass() throws Exception {
        pdao.clear();

        Person[] plist = new Person[3];
        plist[0] = p;
        plist[1] = p2;
        plist[2] = p3;

        assertTrue(pdao.stashMany(plist));

        for (Person prsn : plist) {
            Person result = pdao.find(prsn.getPersonID());
            assertNotNull(result);
            assertEquals(result, prsn);
        }
    }

    /**
     * try adding a list which has a duplicate, this should fail
     */
    @Test(expected = DatabaseException.class)
    public void stashManyFail() throws Exception {
        pdao.clear();

        Person[] plist = new Person[4];
        plist[0] = p;
        plist[1] = p2;
        plist[2] = p3;
        plist[3] = p2;

        assertFalse(pdao.stashMany(plist));
    }

    /** 
     * clear should make there be nothing in the person table 
     */
    @Test
    public void clearPass() throws Exception {
        pdao.clear();

        pdao.stash(p);
        pdao.stash(p2);

        pdao.clear();

        assertNull(pdao.find(p.getPersonID()));
        assertNull(pdao.find(p2.getPersonID()));
    }
    
    /**
     * clearing multiple times in a row isn't a problem
     * @throws Exception
     */
    @Test
    public void clearPassEmpty() throws Exception {
        pdao.clear();
        pdao.clear();
        pdao.clear();
    }

}