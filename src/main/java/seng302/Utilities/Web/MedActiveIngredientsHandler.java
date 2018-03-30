package seng302.Utilities.Web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;

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

    public List<String> getActiveIngredients(String medicationName) {
        List<String> activeIngredients = new ArrayList<>();
        try {
            GenericUrl url = new GenericUrl(String.format(ACTIVE_INGREDIENTS_ENDPOINT, medicationName));
            HttpRequest request = requestFactory.buildGetRequest(url);
            activeIngredients = Arrays.asList(request.execute().parseAs(String[].class));
        } catch (HttpResponseException exc) {
            // TODO handle more gracefully
            exc.printStackTrace();
        } catch (IOException exc) {
            // TODO handle more gracefully
            exc.printStackTrace();
        }
        return activeIngredients;
    }
}
