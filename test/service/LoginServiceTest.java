package service;

import org.junit.*;

import dao.AuthTokenDAO;
import dao.Database;
import dao.model.AuthToken;
import service.ClearService;
import service.LoginService;
import service.RegistrationService;
import service.exception.InvalidLoginException;
import service.exception.ServiceErrorException;
import service.request.LoginRequest;
import service.request.RegistrationRequest;
import service.response.LoginSuccessResponse;
import service.response.MessageResponse;
import service.response.Serializable;

import static org.junit.Assert.*;

/**
 * tests the LoginService
 */
public class LoginServiceTest {
    public LoginServiceTest() {};

    private static LoginRequest GOOD_LOGIN_REQ;
    private static LoginRequest BAD_LOGIN_REQ_USERNAME;
    private static LoginRequest BAD_LOGIN_REQ_PASSWORD;
    private static RegistrationRequest GOOD_REG_REQ;
    private static final String USER1 = "USER1";
    private static final String USER_BAD = "USER2";
    private static final String PWD1 = "secretPWD1";
    private static final String PWD_BAD = "secretPWD2";

    private LoginService loggerInner;
    
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

        GOOD_LOGIN_REQ = new LoginRequest(USER1, PWD1);
        BAD_LOGIN_REQ_USERNAME = new LoginRequest(USER_BAD, PWD1);
        BAD_LOGIN_REQ_PASSWORD = new LoginRequest(USER1, PWD_BAD);
        GOOD_REG_REQ = new RegistrationRequest(USER1, PWD1, "Email", "GivenName", "Surname", "f");
    }

    @Before
    public void setUp() {
        loggerInner = new LoginService();
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
        loggerInner = null;
    }    

    /**
     * test that we can actually log in an existing user
     * @throws Exception if crap happens somewhere
     */
    @Test
    public void loginPass() throws Exception {
        RegistrationService registrar = new RegistrationService();
        Serializable reg = registrar.Register(GOOD_REG_REQ);
        LoginSuccessResponse regResponse = (LoginSuccessResponse)reg;

        Serializable serial = loggerInner.Login(GOOD_LOGIN_REQ);
        LoginSuccessResponse response = (LoginSuccessResponse)serial;

        assertNotNull(response);
        assertEquals(response.getPersonID(), regResponse.getPersonID());

        // make sure the auth token is different
        assertNotEquals(regResponse.getAuthToken(), response.getAuthToken());
        
        // make sure the auth token exists and looks right
        Database.open();
        AuthToken tok;
        try {
            AuthTokenDAO adao = new AuthTokenDAO();
            tok = adao.find(regResponse.getAuthToken());
        } finally {
            Database.close();
        }
            assertNotNull(tok);
            assertEquals(tok.getUserID(), response.getUsername());
    }

    @Test(expected = InvalidLoginException.class)
    public void loginFail_badPassword() throws Exception {
        RegistrationService registrar = new RegistrationService();
        Serializable reg = registrar.Register(GOOD_REG_REQ);
        LoginSuccessResponse regResponse = (LoginSuccessResponse)reg;

        Serializable serial = loggerInner.Login(BAD_LOGIN_REQ_PASSWORD);
    }

    @Test(expected = InvalidLoginException.class)
    public void loginFail_usernameNoExist() throws Exception {
        RegistrationService registrar = new RegistrationService();
        Serializable reg = registrar.Register(GOOD_REG_REQ);
        LoginSuccessResponse regResponse = (LoginSuccessResponse)reg;

        Serializable serial = loggerInner.Login(BAD_LOGIN_REQ_USERNAME);
        
    }
}