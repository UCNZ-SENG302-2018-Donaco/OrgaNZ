package seng302.Utilities.Web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import seng302.Donor;
import seng302.Utilities.Exceptions.BadDrugNameException;
import seng302.Utilities.Exceptions.BadGatewayException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;

/**
 * A handler for requests to a drug interaction API.
 */
public class DrugInteractionsHandler extends WebAPIHandler {
    private static final String INTERACTIONS_ENDPOINT = "https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/";
    private static final int OK = 200;
    private static final int STUDY_NOT_DONE = 202;
    private static final int BAD_DRUG_NAME = 404;
    private static final int BAD_GATEWAY = 502;

    private final HttpRequestFactory requestFactory;

    public DrugInteractionsHandler() {
        this(new NetHttpTransport());
    }

    public DrugInteractionsHandler(HttpTransport httpTransport) {
        super(httpTransport);
        requestFactory = httpTransport.createRequestFactory(request -> {
            request.setParser(new JsonObjectParser(jsonFactory));
            request.setThrowExceptionOnExecuteError(false);
        });
    }

    private GenericUrl createURL(String drug1, String drug2) {
        // Drug names containing '/' will mess with the urls of the web api
        drug1 = drug1.replaceAll("/", "");
        drug2 = drug2.replaceAll("/", "");

        // Drug names with spaces are represented with dashes in the API
        drug1 = drug1.replaceAll(" ", "-");
        drug2 = drug2.replaceAll(" ", "-");

        return new GenericUrl(String.format(INTERACTIONS_ENDPOINT, drug1, drug2));
    }

    private HttpResponse makeRequest(GenericUrl url) throws IOException {
        HttpRequest request = requestFactory.buildGetRequest(url);
        return request.execute();
    }

    public List<String> getInteractions(Donor donor, String drug1, String drug2)
            throws IOException, BadDrugNameException, BadGatewayException {
        try {
            HttpResponse response = makeRequest(createURL(drug1, drug2));
            int statusCode = response.getStatusCode();

            if (statusCode == STUDY_NOT_DONE || statusCode == BAD_GATEWAY) {
                // Try again with drug names reversed to see if we get OK from that.
                response = makeRequest(createURL(drug2, drug1));
                int secondStatusCode = response.getStatusCode();

                if (secondStatusCode != BAD_GATEWAY) {
                    statusCode = secondStatusCode;
                }
            }

            if (statusCode == OK) {
                DrugInteractionsResponse interactionsResponse = response.parseAs(DrugInteractionsResponse.class);
                return interactionsResponse.calculateDonorInteractions(donor);
            } else if (statusCode == STUDY_NOT_DONE) {
                return Collections.emptyList();
            } else if (statusCode == BAD_DRUG_NAME) {
                throw new BadDrugNameException("One or both of the drug names are invalid.");
            } else if (statusCode == BAD_GATEWAY) {
                throw new BadGatewayException("The drug interactions web API could not retrieve the results.");
            } else {
                throw new IllegalArgumentException("The drug interactions API responded in an unexpected way.");
            }
        } catch (IOException exc) {
            throw new IOException("The drug interactions API could not be reached. Check your internet connection and"
                    + " try again.");
        }
    }
}
