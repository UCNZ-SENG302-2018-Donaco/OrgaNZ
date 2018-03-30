package seng302.Utilities.Web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.JsonObjectParser;

/**
 * A handler for requests to the drug autocompletion web API provided by MAPI.
 */
public class MedAutoCompleteHandler implements WebAPIHandler {

    private static final String AUTOCOMPLETE_ENDPOINT = "http://mapi-us.iterar.co/api/autocomplete";

    private HttpRequestFactory requestFactory;

    /**
     * Instantiates a new MedAutoCompleteHandler and sets up its request factory (with a JSON factory for responses).
     */
    public MedAutoCompleteHandler() {
        requestFactory = HTTP_TRANSPORT.createRequestFactory(
                (HttpRequest request) -> request.setParser(new JsonObjectParser(JSON_FACTORY))
        );
    }

    /**
     * Makes a request to the drug autocompletion web API and returns the results.
     * @param queryString The query string to send to the API.
     * @return A list of suggested drug names matching the query string.
     */
    public List<String> getSuggestions(String queryString) {
        List<String> suggestions = new ArrayList<>();
        try {
            MedAutoCompleteURL url = new MedAutoCompleteURL(AUTOCOMPLETE_ENDPOINT);
            url.setQueryString(queryString);
            HttpRequest request = requestFactory.buildGetRequest(url);
            suggestions = request.execute().parseAs(MedAutoCompleteResponse.class).getSuggestions();
        } catch (HttpResponseException exc) {
            // TODO handle more gracefully
            exc.printStackTrace();
        } catch (IOException exc) {
            // TODO handle more gracefully
            exc.printStackTrace();
        }
        return suggestions;
    }
}
