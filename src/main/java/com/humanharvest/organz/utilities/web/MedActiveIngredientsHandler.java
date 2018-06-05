package com.humanharvest.organz.utilities.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.gson.reflect.TypeToken;

/**
 * A handler for requests to the medication active ingredients web API provided by MAPI.
 */
public class MedActiveIngredientsHandler extends WebAPIHandler {

    private static final String ACTIVE_INGREDIENTS_ENDPOINT = "http://mapi-us.iterar.co/api/%s/substances.json";

    private HttpRequestFactory requestFactory;

    /**
     * Instantiates a new MedAutoCompleteHandler using the default NetHttpTransport and sets up its request factory
     * (using a JSON factory to parse JSON response bodies).
     */
    public MedActiveIngredientsHandler() {
        super();
        requestFactory = httpTransport.createRequestFactory(
                (HttpRequest request) -> request.setParser(new JsonObjectParser(jsonFactory))
        );
    }

    /**
     * Instantiates a new MedAutoCompleteHandler using the given HttpTransport (may be mocked) and sets up its request
     * factory (using a JSON factory to parse JSON response bodies).
     */
    public MedActiveIngredientsHandler(HttpTransport transport) {
        super(transport);
        requestFactory = httpTransport.createRequestFactory(
                (HttpRequest request) -> request.setParser(new JsonObjectParser(jsonFactory))
        );
    }

    public List<String> getData(Object... arguments) throws IOException {
        String medicationName;
        if (arguments.length == 1 && arguments[0] instanceof String) {
            medicationName = (String) arguments[0];
        } else {
            throw new UnsupportedOperationException("Must have exactly 1 argument, which is (castable to) a String.");
        }
        return getActiveIngredients(medicationName);
    }

    public List<String> getActiveIngredients(String medicationName) throws IOException {
        Optional<List<String>> cachedResponse = getCachedData(
                new TypeToken<List<String>>() {}.getType()
                , medicationName);
        if (cachedResponse.isPresent()) {
            return cachedResponse.get();
        }

        List<String> activeIngredients;
        try {
            GenericUrl url = new GenericUrl(String.format(ACTIVE_INGREDIENTS_ENDPOINT, medicationName));
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            activeIngredients = addCachedData(Arrays.asList(response.parseAs(String[].class)), medicationName);
        } catch (HttpResponseException exc) {
            // Any non 2xx response (e.g. 404)
            activeIngredients = Collections.emptyList();
        }
        return activeIngredients;
    }
}
