package seng302.Utilities.Web;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.Period;
import java.util.Optional;

import seng302.Utilities.CacheManager;

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
        httpTransport = new NetHttpTransport();
    }

    protected WebAPIHandler(HttpTransport httpTransport) {
        this.httpTransport = httpTransport;
    }

    protected <T> T addCachedData(T value, Object... arguments) {
        Optional<Instant> expires = Optional.of(Instant.now().plus(Period.ofDays(7)));
        CacheManager.INSTANCE.addCachedData(getClass().getTypeName(), arguments, value, expires);
        return value;
    }

    protected <T> Optional<T> getCachedData(Type type, Object... arguments) {
        return CacheManager.INSTANCE.getCachedData(getClass().getTypeName(), type, arguments);
    }
}
