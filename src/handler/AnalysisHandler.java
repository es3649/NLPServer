package handler;

import handler.Handler;
import main.Server;
import service.exception.ServiceErrorException;
import service.response.Serializable;

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
     * all exchanges are authentic for now
     * @return true
     */
    protected boolean isAuthentic(HttpExchange exchange) {
        return true;
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
        return null;
    }
}