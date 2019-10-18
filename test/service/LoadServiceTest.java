package service;

import org.junit.*;

import dao.AuthTokenDAO;
import dao.Database;
import dao.EventDAO;
import dao.PersonDAO;
import dao.UserDAO;
import dao.model.AuthToken;
import dao.model.Event;
import dao.model.Person;
import dao.model.User;
import main.Server;
import service.ClearService;
import service.LoadService;
import service.RegistrationService;
import service.exception.ServiceErrorException;
import service.generator.Generator;
import service.request.Deserializer;
import service.request.LoadRequest;
import service.request.RegistrationRequest;
import service.response.MessageResponse;
import service.response.Serializable;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LoadServiceTest {
    public LoadServiceTest() {};

    private LoadService loader;
    public static LoadRequest GOOD_REQ;

    @BeforeClass
    public static void setUpAll() {
        ClearService clearer = new ClearService();
        MessageResponse clearResp;
        try {
            clearResp = clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("\nSorry!! I left a mess in the database...");
            System.out.printf("Reason: %s\n", ex.getMessageResponse().Serialize());
        }

        try {
            File testRequest = new File("data/json/example.json");
            FileInputStream inFile = new FileInputStream(testRequest);
            String json = new String(inFile.readAllBytes());
            Deserializer d = new Deserializer();
            GOOD_REQ = d.loadRequest(json);
        } catch (Exception ex) {
            System.out.println("Failed to load LoadRequest json");
            Server.logger.log(Level.SEVERE, "Failed to load LoadRequest json", ex);
        }
    }

    @Before
    public void setUp() {
        loader = new LoadService();
    }

    @After
    public void cleanUp() {
        ClearService clearer = new ClearService();
        MessageResponse clearResp;
        try {
            clearResp = clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("\nSorry!! I left a mess in the database...");
            System.out.printf("Reason: %s\n", ex.getMessageResponse().Serialize());
        }
    }

    @Test
    public void loadTestPass() throws Exception {
        // put some data in the DB
        List<Person> plist = new ArrayList<Person>();
        List<Event> elist = new ArrayList<Event>();
        Person p = new Person("user", "firstName", "lastName", "f");

        Generator gen = new Generator();
        gen.Generate(p, 2, plist, elist);

        try {
            Database.open();
            PersonDAO pdao = new PersonDAO();
            pdao.stashMany(plist.toArray(new Person[0]));

            EventDAO edao = new EventDAO();
            edao.stashMany(elist.toArray(new Event[0]));

            Database.commit();
        } finally {
            Database.rollback();
            Database.close();
        }

        loader.Load(GOOD_REQ);

        // assert that the old guys are no longer in the DB
        try {
            Database.open();

            PersonDAO pdao = new PersonDAO();
            EventDAO edao = new EventDAO();
            // check that the new stuff is actually in there
            for (Person prsn : GOOD_REQ.getPersonsToLoad()) {
                Person inDB = pdao.find(prsn.getPersonID());
                assertNotNull(inDB);
                assertEquals(inDB, prsn);
            }

            for (Event evnt : GOOD_REQ.getEventsToLoad()) {
                Event inDB = edao.find(evnt.getEventID());
                assertNotNull(inDB);
                assertEquals(inDB, evnt);
            }

            UserDAO udao = new UserDAO();
            for (User user : GOOD_REQ.getUsersToLoad()) {
                User inDB = udao.find(user.getUsername());
                assertNotNull(inDB);
                assertEquals(inDB, user);
            }

        } finally {
            Database.close();
        }

    }

    /**
     * make sure the previously existing data is removed from the DB
     * once the new data is loaded
     * @throws Exception
     */
    @Test
    public void loadTest_dataOverwritten() throws Exception {

        // put some data in the DB
        List<Person> plist = new ArrayList<Person>();
        List<Event> elist = new ArrayList<Event>();
        Person p = new Person("user", "firstName", "lastName", "f");
        User u = new User("Username", "Password", "Email", "GivenName", "Surname", "m");
        u.MakePerson();
        AuthToken tok = new AuthToken("Username");

        Generator gen = new Generator();
        gen.Generate(p, 2, plist, elist);

        try {
            Database.open();
            PersonDAO pdao = new PersonDAO();
            pdao.stashMany(plist.toArray(new Person[0]));

            EventDAO edao = new EventDAO();
            edao.stashMany(elist.toArray(new Event[0]));

            UserDAO udao = new UserDAO();
            udao.stash(u);

            AuthTokenDAO adao = new AuthTokenDAO();
            adao.stash(tok);

            Database.commit();
        } finally {
            Database.rollback();
            Database.close();
        }

        loader.Load(GOOD_REQ);

        try {
            Database.open();
            PersonDAO pdao = new PersonDAO();
            for (Person prsn : plist) {
                assertNull(pdao.find(prsn.getPersonID()));
            }

            EventDAO edao = new EventDAO();
            for (Event evnt : elist) {
                assertNull(edao.find(evnt.getEventID()));
            }

            UserDAO udao = new UserDAO();
            assertNull(udao.find(u.getUsername()));

            AuthTokenDAO adao = new AuthTokenDAO();
            assertNull(adao.find(tok.getTokenString()));
            
        } finally {
            Database.close();
        }
    }
}