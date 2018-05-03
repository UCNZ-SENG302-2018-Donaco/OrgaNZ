package seng302.Utilities.Web;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

/**
 * An abstract class for a generic WebAPIHandler.
 * Holds the underlying HttpTransport client and JSON factory for all WebAPIHandlers to use.
 */
public abstract class WebAPIHandler {

    protected HttpTransport httpTransport;
    protected JsonFactory jsonFactory = new GsonFactory();

    protected WebAPIHandler() {
        this.httpTransport = new NetHttpTransport();
    }

    protected WebAPIHandler(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }
}
