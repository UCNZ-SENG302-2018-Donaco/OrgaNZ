package seng302.Utilities.Web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;

public class HTTPUtils {

    private static final String AUTOCOMPLETE_ENDPOINT = "http://mapi-us.iterar.co/api/autocomplete";

    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = new GsonFactory();

    public static List<String> getMedAutoCompleteSuggestions(String queryString) {
        List<String> suggestions = new ArrayList<>();

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(
                (HttpRequest request) -> request.setParser(new JsonObjectParser(JSON_FACTORY))
        );

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

    public static void main(String[] args) {
        System.out.println(getMedAutoCompleteSuggestions("pan"));
    }
}
