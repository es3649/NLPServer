package dao;

import org.junit.*;

import dao.AuthTokenDAO;
import dao.Database;
import dao.DatabaseException;
import dao.PersonDAO;
import dao.model.AuthToken;
import dao.model.Person;

import static org.junit.Assert.*;

import java.sql.ResultSet;

public class AuthTokenDAOTest {
    public AuthTokenDAOTest() {};
    
    @Before
    public void setUp() {
        adao = new AuthTokenDAO();
        
        // make a new person
        tok = new AuthToken("mjolnir slinger");
        tok2 = new AuthToken("danny_boi");
        tok3 = new AuthToken("danny_boi");

        try {
            Database.open();
            adao.clear();
        } catch (DatabaseException ex) {
            // ex.printStackTrace(System.err);
        }
    }

    static AuthTokenDAO adao;
    static AuthToken tok;
    static AuthToken tok2;
    static AuthToken tok3;

    @After
    public void cleanUp() {
        Database.close();
    }
    
    /**
     * Testes that insertion works
     * @throws Exception
     */
    @Test
    public void stashPass() throws Exception {
        boolean success = false;
        
        adao.clear();
        success = adao.stash(tok);
        // Database.commit();

        assertTrue(success);
    }

    /**
     * We try to stash the same token again:
     * this should violate the unique key constraint
     * @throws Exception
     */
    @Test(expected = DatabaseException.class)
    public void stashFail() throws Exception {
        adao.clear();
        // stash him twice
        adao.stash(tok);
        adao.stash(tok);
    }

    /**
     * we store the token, the look it up again and assert that we actually found something
     */
    @Test
    public void findPass() throws Exception {
        adao.clear();
        adao.stash(tok);
        AuthToken found = adao.find(tok.getTokenString());

        assertNotNull(found);
        assertEquals(tok, found);
    }

    /**
     * we attempt to find a person in an empty database
     */
    @Test
    public void findFail() throws Exception {
        adao.clear();
        AuthToken found = adao.find(tok.getTokenString());

        assertNull(found);
    }

    /**
     * we attempt to remove the person immediately after adding them
     */
    @Test
    public void removePass() throws Exception {
        adao.clear();
        adao.stash(tok);
        int deleted = adao.remove(tok.getTokenString());

        assertEquals(1, deleted);
    }

    /**
     * we attempt to remove the person from the empty database
     */
    @Test
    public void removeFail() throws Exception {
        adao.clear();
        int deleted = adao.remove(tok.getTokenString());

        assertEquals(0, deleted);
    }

    /**
     * ensures that the exists function works. Since it wraps find it ought to be fine
     */
    @Test
    public void existsPass() throws Exception {
        // put the stuff in
        adao.clear();
        adao.stash(tok);
        Database.commit();
        Database.close();

        // do the test
        String user = adao.exists(tok.getTokenString());
        assertNotNull(user);
        assertEquals(user, tok.getUserID());

        // take the stuff out
        Database.open();
        adao.clear();
    }

    /**
     * fail to find a nonexistant token
     * @throws Exception
     */
    @Test
    public void existsFail() throws Exception {
        // put the stuff in
        adao.clear();
        Database.close();

        // do the test
        String user = adao.exists(tok.getTokenString());
        assertNull(user);

        // take the stuff out
        Database.open();
        adao.clear();
    }

    /**
     * there should be nothing left in the DB after we clear it
     * @throws Exception
     */
    @Test
    public void clearPass() throws Exception {
        adao.clear();

        adao.stash(tok);
        adao.stash(tok2);

        adao.clear();

        assertNull(adao.find(tok.getTokenString()));
        assertNull(adao.find(tok2.getTokenString()));
    }

    /**
     * we should be able to call this multiple times in a row, no problem
     * @throws Exception
     */
    @Test
    public void clearPassEmpty() throws Exception {
        adao.clear();
        adao.clear();
        adao.clear();
    }
}