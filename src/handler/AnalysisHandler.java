package handler;

import handler.Handler;
import main.PasswordProvider;
import main.Server;
import service.AnalysisService;
import service.exception.ServiceErrorException;
import service.request.AnalysisRequest;
import service.response.AnalysisResponse;
import service.response.MessageResponse;
import service.response.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;


public class AnalysisHandler extends Handler {
    /**
     * This handler accepts POST requests
     * @return the literal string "POST"
     */
    protected String properRequestMethod() {
        return Handler.POST_REQUEST_METHOD;
    }

    /**
     * Evaluartes the password hashed, generated with SHA256
     * @return true if the passwords hash received and generated are identical
     *         false if they are not
     */
    protected boolean isAuthentic(HttpExchange exchange) {
        // get the request headers
        Headers requestHeaders = exchange.getRequestHeaders();

        // check if the header even exists
        if (!requestHeaders.containsKey(PasswordProvider.PASSWORD_REQUEST_HEADER)) {
            return false;
        }

        // get the bytestring of the hash value from the headers
        byte[] receivedHash = requestHeaders.getFirst(PasswordProvider.PASSWORD_REQUEST_HEADER).getBytes();

        // calcuate the hash of the known password
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] passwordHash = digest.digest(Server.password.APIPassword().getBytes(StandardCharsets.UTF_8));

            // compare hashes and return
            return Arrays.equals(receivedHash, passwordHash);
        } catch (NoSuchAlgorithmException ex) {
            Server.logger.log(Level.SEVERE, "Hash algorithm not found", ex);
        }
        
        return false;
    }

    /**
     * This is where all the hard work starts:
     *   deserialize the http request body
     *   begin the processing
     *   return the result
     * @param exchange the http exchange
     * @return the result of the NLP methods
     */
    protected Serializable handleAndServe(HttpExchange exchange) throws ServiceErrorException {
        AnalysisService service = new AnalysisService();

        // get the Input stream from the server
        InputStream requestBody = exchange.getRequestBody();
        try {
            // read the string from the request and create a request object
            String requestJson = readAllBytes(requestBody);
            AnalysisRequest req = AnalysisRequest.fromJson(requestJson);

            return service.serve(req);
        } catch (IOException ex) {
            return new MessageResponse("Failed to get request body");
        }

    }

    /**
     * Reads all bytes from an input stream, returning them as a single string
     * @param is the input stream to read from
     * @return a string representing all the bytes
     * @throws IOException if we can't read the InputStream
     */
    private String readAllBytes(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buffer = new char[1024];
        int len;
        while ((len = sr.read(buffer)) > 0) {
            sb.append(buffer, 0, len);
        }
        return sb.toString();
    }
}