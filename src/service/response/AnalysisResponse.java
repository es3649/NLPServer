package service.response;

import com.google.gson.Gson;

/**
 * The response object for the analysis response
 */
public class AnalysisResponse implements Serializable {
    public AnalysisResponse() {}

    /**
     * Uses GSON to serialize the response
     * @return a json representation of the response
     */
    @Override
    public String Serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}