package service.request;

import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import main.Server;

import com.google.gson.JsonSyntaxException;

import service.exception.BadRequestException;
import service.response.MessageResponse;

/** Deserializer deserializes requests which the handlers receive
 */
public class Deserializer {
    /**
     * Constructs a Deserializer
     */
    public Deserializer() {}

    /**
     * Constructs a <code>LoginRequest</code> object from a json string
     * @param requestMessage the json string containing the 
     * @return the deserialized <code>LoginRequest</code>,
     *          or <code>null</code> if deserialization failed
     * @throws BadRequestException if the json doesn't parse
     */
    public LoginRequest loginRequest(String requestMessage) 
            throws BadRequestException {
        LoginRequest req = new LoginRequest();
        Gson gson = new Gson();
        TypeToken<LoginRequest> typTok = new TypeToken<LoginRequest>() {};
        try {
            req = gson.fromJson(requestMessage, typTok.getType());
        } catch (JsonSyntaxException ex) {
            throw new BadRequestException(
                new MessageResponse("Unable to parse request body"), ex);
        }

        Server.logger.log(Level.INFO, String.format(
            "Parsed LoginRequest from Json: %s", req.toString()));

        // validate
        if (!req.isValid()) {
            throw new BadRequestException(
                new MessageResponse("Request has null or empty values"));
        }

        return req;
    }
};
