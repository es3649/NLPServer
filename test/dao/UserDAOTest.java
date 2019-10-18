package dao;

import org.junit.*;
import junit.framework.TestCase;

import dao.model.Person;
import dao.model.User;

import static org.junit.Assert.*;

public class UserDAOTest {
    public UserDAOTest() {};

    static UserDAO udao;
    static User u;
    static User u2;
    static User u3;

    @Before
    public void setUp() {
        udao = new UserDAO();
        
        // make a new person
        u = new User("hammer_swinger", "janeishot", "N/A", "Thor", "Son of Odin", "m");
        u2 = new User("danny_boi", "pwd", "example@cd.bgr", "danny", "boi", "m");
        u3 = new User("tom_katz", "secret", "emailn00b", "kitty", "clarkson", "f");
        Person p = u.MakePerson();
        u.setPersonID(p.getPersonID());
        u2.MakePerson();
        u3.MakePerson();
        PersonDAO udao = new PersonDAO();
        
        try {
            // acquire the database
            Database.open();
            udao.stash(p);
            udao.clear();
            Database.commit();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }

    }

    @After
    public void cleanup() {
        try {
            PersonDAO udao = new PersonDAO();
            udao.clear();
            udao.clear();
            Database.commit();
            Database.close();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }
    }

    /**
     * add hammer_swinger to the database
     */
    @Test
    public void stashPass() throws Exception {
        boolean success = false;
        try {
            udao.clear();
            success = udao.stash(u);
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }

        assertTrue(success);
    }

    /**
     * try to add hammer_swinger twice, this should fail by uniqueness
     */
    @Test(expected = DatabaseException.class)
    public void stashFail() throws Exception {
        udao.clear();
        udao.stash(u);
        udao.stash(u);
    }

    /**
     * add our guy to the database and 
     */
    @Test
    public void findPass() throws Exception {
        udao.clear();
        udao.stash(u);
        User result = udao.find(u.getUsername());

        assertNotNull(result);
        assertEquals(u, result);
    }

    /**
     * look for our guy in an empty database
     */
    @Test
    public void findFail() throws Exception {
        udao.clear();
        User result = udao.find(u.getUsername());

        assertNull(result);
    }

    /**
     * we add the user then remove him. Should pass
     */
    @Test
    public void removePass() throws Exception {
        udao.clear();
        udao.stash(u);
        int changed = udao.remove(u.getUsername());

        assertEquals(changed, 1);
    }

    /**
     * we try to remove our user from the empty database
     */
    @Test
    public void removeFail() throws Exception {
        udao.clear();
        int changed = udao.remove(u.getUsername());

        assertEquals(changed, 0);
    }

    /**
     * we can add many users at once: they all get added
     * @throws Exception
     */
    @Test
    public void stashManyPass() throws Exception {
        udao.clear();

        User[] ulist = new User[3];
        ulist[0] = u;
        ulist[1] = u2;
        ulist[2] = u3;

        assertTrue(udao.stashMany(ulist));

        for (User usr : ulist) {
            User result = udao.find(usr.getUsername());
            assertNotNull(result);
            assertEquals(result, usr);
        }
    }

    /**
     * try adding a list which has a duplicate, this should fail
     */
    @Test(expected = DatabaseException.class)
    public void stashManyFail() throws Exception {
        udao.clear();

        User[] ulist = new User[4];
        ulist[0] = u;
        ulist[1] = u2;
        ulist[2] = u3;
        ulist[3] = u2;

        assertFalse(udao.stashMany(ulist));
    }

    /** 
     * clear should make there be nothing in the person table 
     */
    @Test
    public void clearPass() throws Exception {
        udao.clear();

        udao.stash(u);
        udao.stash(u2);

        udao.clear();

        assertNull(udao.find(u.getUsername()));
        assertNull(udao.find(u2.getUsername()));
    }
    
    /**
     * clearing multiple times in a row isn't a problem
     * @throws Exception
     */
    @Test
    public void clearPassEmpty() throws Exception {
        udao.clear();
        udao.clear();
        udao.clear();
    }
}