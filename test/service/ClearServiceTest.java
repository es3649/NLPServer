package service;

import org.junit.*;

import dao.AuthTokenDAO;
import dao.Database;
import dao.DatabaseException;
import dao.EventDAO;
import dao.PersonDAO;
import dao.UserDAO;
import dao.model.AuthToken;
import dao.model.Event;
import dao.model.Person;
import dao.model.User;
import service.ClearService;
import service.response.MessageResponse;

import static org.junit.Assert.*;

/**
 * this class tests that the clear function is working,
 * it only implements passing tests, but since this shouldn't 
 * fail on any condition, there's no way to have a failing test.
 */
public class ClearServiceTest {
    public ClearServiceTest() {};

    public static final MessageResponse CORRECT = 
        new MessageResponse("Clear succeeded");

    @Test
    public void clearEventsPass() throws Exception {
        Database.open();

        EventDAO edao = new EventDAO();
        Event e1 = new Event();
        e1.setCity("Orem");
        e1.setCountry("USA");
        e1.setEventType("Death");
        e1.setLatitude(0);
        e1.setLongitude(500.354f);
        e1.setPersonID("jimmy57");
        e1.setDescendant("jimmy57");
        e1.setYear(2020);
        Event e2 = new Event();
        e2.setCity("Tianjin");
        e2.setCountry("China");
        e2.setEventType("Death");
        e2.setLatitude(29.3f);
        e2.setLongitude(94.52f);
        e2.setPersonID("jimmy58");
        e2.setDescendant("jimmy57");
        e2.setYear(2021);

        edao.stash(e1);
        edao.stash(e2);
        Database.commit();
        Database.close();

        ClearService clearer = new ClearService();
        MessageResponse resp = clearer.Clear();
        assertEquals(resp, CORRECT);

        Database.open();

        Event result = edao.find(e1.getEventID());
        assertNull(result);
        result = edao.find(e2.getEventID());
        assertNull(result);

        edao.clear();
        Database.close();
    }

    @Test
    public void clearPersonsPass() throws Exception {
        Database.open();

        PersonDAO pdao = new PersonDAO();
        Person p1 = new Person("bob_ross3", "bob", "ross", "m");
        Person p2 = new Person("mischeifGod", "Loki", "Hiddleston", "m");

        pdao.stash(p1);
        pdao.stash(p2);
        Database.commit();
        Database.close();

        ClearService clearer = new ClearService();
        MessageResponse resp = clearer.Clear();
        assertEquals(resp, CORRECT);

        Database.open();
        
        Person result = pdao.find(p1.getPersonID());
        assertNull(result);
        result = pdao.find(p2.getPersonID());
        assertNull(result);

        pdao.clear();
        Database.close();
    }

    @Test
    public void clearUsersPass() throws Exception {
        Database.open();

        UserDAO udao = new UserDAO();
        User u1 = new User("mj5", "moonwalk4dayz", 
            "michael.jackson@gmail.gov", "michael", "jackson", "m");
        u1.setPersonID("p1");
        User u2 = new User("U2", "rockmusicrocks", 
            "guitar.hero@U2.com", "jimi", "hendrix", "m");
        u2.setPersonID("p1");

        udao.stash(u1);
        udao.stash(u2);
        Database.commit();
        Database.close();

        ClearService clearer = new ClearService();
        MessageResponse resp = clearer.Clear();
        assertEquals(resp, CORRECT);

        Database.open();

        User result = udao.find(u1.getUsername());
        assertNull(result);
        result = udao.find(u2.getUsername());
        assertNull(result);

        udao.clear();
        Database.close();
    }

    @Test
    public void clearAuthTokensPass() throws Exception {
        Database.open();

        AuthTokenDAO adao = new AuthTokenDAO();
        AuthToken tok1 = new AuthToken("user1");
        AuthToken tok2 = new AuthToken("user2");

        adao.stash(tok1);
        adao.stash(tok2);
        Database.commit();
        Database.close();

        ClearService clearer = new ClearService();
        MessageResponse resp = clearer.Clear();
        assertEquals(resp, CORRECT);

        Database.open();

        AuthToken result = adao.find(tok1.getTokenString());
        assertNull(result);
        result = adao.find(tok2.getTokenString());
        assertNull(result);

        adao.clear();
        Database.close();
    }
}