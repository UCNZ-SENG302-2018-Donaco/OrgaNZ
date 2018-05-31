package com.humanharvest.organz.utilities.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.gson.reflect.TypeToken;

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

    /**
     * Instantiates a new DrugInteractionsHandler using the default NetHttpTransport and sets up its request factory
     * (using a JSON factory to parse JSON response bodies).
     */
    public DrugInteractionsHandler() {
        this(new NetHttpTransport());
    }

    /**
     * Instantiates a new DrugInteractionsHandler using the given HttpTransport (may be mocked) and sets up its request
     * factory (using a JSON factory to parse JSON response bodies).
     */
    public DrugInteractionsHandler(HttpTransport httpTransport) {
        super(httpTransport);
        requestFactory = httpTransport.createRequestFactory(request -> {
            request.setParser(new JsonObjectParser(jsonFactory));
            request.setThrowExceptionOnExecuteError(false);
        });
    }

    /**
     * Creates a URL for a request to the drug interactions API with the given drug names. Will sanitise the drug
     * name inputs so that they work correctly with the API.
     * @param drug1 The first drug name to use for the request.
     * @param drug2 The second drug name to use for the request.
     * @return The URL for the request.
     */
    private static GenericUrl createURL(String drug1, String drug2) {
        // Drug names containing '/' will mess with the urls of the web api
        drug1 = drug1.replaceAll("/", "");
        drug2 = drug2.replaceAll("/", "");

        // Drug names with spaces are represented with dashes in the API
        drug1 = drug1.replaceAll(" ", "-");
        drug2 = drug2.replaceAll(" ", "-");

        return new GenericUrl(String.format(INTERACTIONS_ENDPOINT, drug1, drug2));
    }

    /**
     * Makes a GET request to the given URL and returns the {@link HttpResponse}.
     * @param url The URL to send a GET request to.
     * @return The response from the given URL.
     * @throws IOException If the server at the URL cannot be reached, e.g. if there is no internet access.
     */
    private HttpResponse makeRequest(GenericUrl url) throws IOException {
        HttpRequest request = requestFactory.buildGetRequest(url);
        return request.execute();
    }

    public List<String> getData(Object... arguments) throws IOException, BadDrugNameException, BadGatewayException {
        String drug1, drug2;
        if (arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof String) {
            drug1 = (String) arguments[0];
            drug2 = (String) arguments[1];
        } else {
            throw new UnsupportedOperationException(
                    "Must have exactly 2 argument, which are both (castable to) a String.");
        }
        //todo pass in a client
        return getInteractions(new Client(), drug1, drug2);
    }

    /**
     * Makes a request to the drug interactions web API and returns a list of interactions between the two drugs
     * given that apply for the given client (based on age and gender).
     * @param client The client to check that the interactions apply for.
     * @param drug1 The name of the first drug to find interactions for.
     * @param drug2 The name of the second drug to find interactions for.
     * @return A list of strings that each contain the details of one interaction symptom. May be empty if there are
     * no results for that request.
     * @throws IOException If the drug interactions web API cannot be reached, e.g. if there is no internet access.
     * @throws BadDrugNameException If the API returns a 404 response saying that the drug names are invalid.
     * @throws BadGatewayException If the API returns a 502 response.
     */
    public List<String> getInteractions(Client client, String drug1, String drug2)
            throws IOException, BadDrugNameException, BadGatewayException {
        Optional<DrugInteractionsResponse> cachedResponse = getCachedData(new TypeToken<DrugInteractionsResponse>() {
        }.getType(), drug1, drug2);
        if (cachedResponse.isPresent()) {
            return cachedResponse.get().calculateClientInteractions(client);
        }

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

            return handleInteractionsResponse(client, response, statusCode, drug1, drug2);
        } catch (IOException exc) {
            throw new IOException("The drug interactions API could not be reached. "
                    + "Check your internet connection and try again.", exc);
        }
    }

    /**
     * Using the response data, returns a formatted interaction response.
     */
    private List<String> handleInteractionsResponse(Client client, HttpResponse response, int statusCode,
            String drug1, String drug2)
            throws IOException, BadDrugNameException, BadGatewayException {
        switch (statusCode) {
            case OK:
                DrugInteractionsResponse interactionsResponse = response.parseAs(DrugInteractionsResponse.class);
                addCachedData(interactionsResponse, drug1, drug2);
                return interactionsResponse.calculateClientInteractions(client);
            case STUDY_NOT_DONE:
                addCachedData(DrugInteractionsResponse.EMPTY, drug1, drug2);
                return Collections.emptyList();
            case BAD_DRUG_NAME:
                throw new BadDrugNameException("One or both of the drug names are invalid.");
            case BAD_GATEWAY:
                throw new BadGatewayException("The drug interactions web API could not retrieve the results.");
            default:
                throw new IllegalArgumentException("The drug interactions API responded in an unexpected way.");
        }
    }
}
