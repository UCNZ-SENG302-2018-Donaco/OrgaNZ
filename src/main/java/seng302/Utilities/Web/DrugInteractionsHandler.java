package seng302.Utilities.Web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import seng302.Donor;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;

/**
 * A handler for requests to a drug interaction API.
 */
public class DrugInteractionsHandler extends WebAPIHandler {
    private static final String INTERACTIONS_ENDPOINT = "https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/";
    private static final int STUDY_NOT_DONE_RESPONSE = 202;
    private static final int BAD_DRUG_NAME = 404;

    private final HttpRequestFactory requestFactory;

    public DrugInteractionsHandler() {
        this(new NetHttpTransport());
    }

    public DrugInteractionsHandler(HttpTransport httpTransport) {
        super(httpTransport);
        requestFactory = httpTransport.createRequestFactory(request -> {
            request.setParser(new JsonObjectParser(jsonFactory));
        });
    }

    public List<String> getInteractions(Donor donor, String drug1, String drug2) {
        // TODO: Use drug object instead of String?

        if (donor == null) {
            throw new IllegalArgumentException("donor must not be null");
        }

        if (drug1 == null) {
            throw new IllegalArgumentException("drug1 must not be null");
        }

        if (drug2 == null) {
            throw new IllegalArgumentException("drug2 must not be null");
        }

        // Drug names containing / will mess with the urls of the web api
        assert !drug1.contains("/");
        assert !drug2.contains("/");

        GenericUrl url = new GenericUrl(String.format(INTERACTIONS_ENDPOINT, drug1, drug2));
        try {
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            int statusCode = response.getStatusCode();

            if (statusCode == STUDY_NOT_DONE_RESPONSE) {
                // TODO: Log something about study not being done
                return Collections.emptyList();
            } else {
                DrugInteractionsResponse interactionsResponse = response.parseAs(DrugInteractionsResponse.class);
                return interactionsResponse.calculateDonorInteractions(donor);
            }
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == BAD_DRUG_NAME) {
                throw new IllegalArgumentException("One or both of the drug names are invalid.", e);
            }

            // TODO handle more gracefully
            e.printStackTrace();

            throw new UnsupportedOperationException(e);
        } catch (IOException e) {
            // TODO handle more gracefully
            e.printStackTrace();

            throw new UnsupportedOperationException(e);
        }
    }
}
