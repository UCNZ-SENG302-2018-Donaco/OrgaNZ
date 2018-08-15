package com.humanharvest.organz.utilities.web;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A handler for requests to the drug autocompletion web API provided by MAPI.
 */
public class MedAutoCompleteHandler extends WebAPIHandler {

    private static final Logger LOGGER = Logger.getLogger(MedAutoCompleteHandler.class.getName());

    private static final String AUTOCOMPLETE_ENDPOINT = "http://mapi-us.iterar.co/api/autocomplete";

    private HttpRequestFactory requestFactory;

    /**
     * Instantiates a new MedAutoCompleteHandler using the default NetHttpTransport and sets up its request factory
     * (using a JSON factory to parse JSON response bodies).
     */
    public MedAutoCompleteHandler() {
        super();
        requestFactory = httpTransport.createRequestFactory(
                (HttpRequest request) -> request.setParser(new JsonObjectParser(jsonFactory))
        );
    }

    /**
     * Instantiates a new MedAutoCompleteHandler using the given HttpTransport (may be mocked) and sets up its request
     * factory (using a JSON factory to parse JSON response bodies).
     */
    public MedAutoCompleteHandler(HttpTransport transport) {
        super(transport);
        requestFactory = httpTransport.createRequestFactory(
                (HttpRequest request) -> request.setParser(new JsonObjectParser(jsonFactory))
        );
    }

    public List<String> getData(Object... arguments) {
        String queryString;
        if (arguments.length == 1 && arguments[0] instanceof String) {
            queryString = (String) arguments[0];
        } else {
            throw new UnsupportedOperationException("Must have exactly 1 argument, which is (castable to) a String.");
        }
        return getSuggestions(queryString);
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
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
        return suggestions;
    }
}
