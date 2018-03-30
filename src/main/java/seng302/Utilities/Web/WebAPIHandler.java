package seng302.Utilities.Web;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

/**
 * An interface for a generic WebAPIHandler.
 * Holds the underlying HttpTransport client and JSON factory for all WebAPIHandlers to use.
 */
public interface WebAPIHandler {

    HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    JsonFactory JSON_FACTORY = new GsonFactory();
}
