package service.request;

import org.junit.*;

import dao.model.Event;
import dao.model.Person;
import dao.model.User;
import main.Server;
import service.ClearService;
import service.exception.ServiceErrorException;
import service.request.Deserializer;
import service.request.LoadRequest;
import service.request.LoginRequest;
import service.request.RegistrationRequest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * tests the deserializer class
 */
public class DeserializerTest {
    public DeserializerTest() {};

    final static String GOOD_RGSTRTN_RQST_JSON = 
    "{\"userName\":\"default_user\",\"password\":\"default_pwd\","
    + "\"email\":\"example@email.com\",\"firstName\":\"jane\","
    + "\"lastName\":\"doe\",\"gender\":\"f\"}";
    static RegistrationRequest GOOD_RGSTRN_REQ;
    final static String GOOD_LGN_RQST_JSON = 
    "{\"userName\":\"default_user\",\"password\":\"default_pwd\"}";
    static LoginRequest GOOD_LGN_RQST;
    static String GOOD_LOAD_RQST_JSON;
    static Deserializer dsrlzr;

    @BeforeClass
    public static void initializeAll() {
        // initialize the actual request objects
        GOOD_LGN_RQST = new LoginRequest("default_user", "default_pwd");
        GOOD_RGSTRN_REQ = new RegistrationRequest("default_user", "default_pwd", 
        "example@email.com", "jane", "doe", "f");
        
        try {
            ClearService clearer = new ClearService();
            clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("Sorry!! Left a mess in the db!");
        }
        
        try {
            File testRequest = new File("data/json/example.json");
            FileInputStream inFile = new FileInputStream(testRequest);
            GOOD_LOAD_RQST_JSON = new String(inFile.readAllBytes());
        } catch (IOException ex) {
            System.out.println("Failed to load LoadRequest json");
            Server.logger.log(Level.SEVERE, "Failed to load LoadRequest json", ex);
        }
    }

    // TODO add failing tests

    @Before
    public void setUp() {
        dsrlzr = new Deserializer();
    }

    @After
    public void cleanUp() {
        dsrlzr = null;
        try {
            ClearService clearer = new ClearService();
            clearer.Clear();
        } catch (ServiceErrorException ex) {
            System.out.println("Sorry!! Left a mess in the db!");
        }
    }

    @Test
    public void registrationRequestPass() throws Exception {
        RegistrationRequest req = 
            dsrlzr.registrationRequest(GOOD_RGSTRTN_RQST_JSON);
        
        // these should be the same
        assertEquals(req, GOOD_RGSTRN_REQ);
    }

    @Test
    public void loginRequestPass() throws Exception {
        LoginRequest req = 
            dsrlzr.loginRequest(GOOD_LGN_RQST_JSON);

        // these should be equal
        assertEquals(req, GOOD_LGN_RQST);
    }

    @Test
    public void loadRequestPass() throws Exception {
        LoadRequest req = 
            dsrlzr.loadRequest(GOOD_LOAD_RQST_JSON);
        
        // it ought to be sufficient to test that everything is valid
        assertNotNull(req);
        assertTrue(req.isValid());
        assertTrue(req.getEventsToLoad().length == 2);
        assertTrue(req.getUsersToLoad().length == 1);
        assertTrue(req.getPersonsToLoad().length == 3);
    }
}