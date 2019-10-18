package service;

import org.junit.*;

import dao.AuthTokenDAO;
import dao.Database;
import dao.PersonDAO;
import dao.UserDAO;
import dao.model.AuthToken;
import dao.model.Person;
import dao.model.User;
import service.exception.ServiceErrorException;
import service.request.RegistrationRequest;
import service.response.LoginSuccessResponse;
import service.response.MessageResponse;
import service.response.Serializable;

import static org.junit.Assert.*;


/**
 * Tests the registration service
 */
public class RegistrationServiceTest {
    public RegistrationServiceTest() {};

    static RegistrationRequest GOOD_RGSTRN_REQ;
    static RegistrationService registrar;

    @BeforeClass
    public static void setUpAll() {
        GOOD_RGSTRN_REQ = new RegistrationRequest("default_user", "default_pwd", 
        "example@email.com", "jane", "doe", "f");
    }

    @AfterClass
    public static void cleanUpAll() {
        ClearService clearer = new ClearService();
        MessageResponse clearResp;
        try {
            clearResp = clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("\nSorry!! I left a mess in the database...");
            System.out.printf("Reason: %s\n", ex.getMessageResponse().Serialize());
        }
    }

    @Before
    public void setUp() {
        registrar = new RegistrationService();
    }

    @After
    public void cleanUp() {
        registrar = null;
    }

    @Test
    public void registerPass() throws Exception {
        Serializable responseRaw = registrar.Register(GOOD_RGSTRN_REQ);

        LoginSuccessResponse response = (LoginSuccessResponse)responseRaw;

        assertEquals(response.getUsername(), GOOD_RGSTRN_REQ.getUsername());
        assertNotNull(response.getAuthToken());
        assertNotNull(response.getPersonID());

        Database.open();
        
        // look up the person they said that they gave us and make sure it's legit
        PersonDAO pdao = new PersonDAO();
        Person p = pdao.find(response.getPersonID());
        
        // look up the auth token and make sure it exists
        AuthTokenDAO adao = new AuthTokenDAO();
        AuthToken tok = adao.find(response.getAuthToken());
        
        // make sure the user was actually added to the DB
        UserDAO udao = new UserDAO();
        User u = udao.find(response.getUsername());
        
        Database.close();

        assertNotNull(p);
        assertEquals(p.getPertinentUser(), GOOD_RGSTRN_REQ.getUsername());
        assertNotNull(tok);
        assertEquals(tok.getUserID(), GOOD_RGSTRN_REQ.getUsername());
        assertNotNull(u);
    }

    @Test(expected = ServiceErrorException.class)
    public void registerFailUnique() throws Exception {
        registrar.Register(GOOD_RGSTRN_REQ);
        registrar.Register(GOOD_RGSTRN_REQ);
    }
}