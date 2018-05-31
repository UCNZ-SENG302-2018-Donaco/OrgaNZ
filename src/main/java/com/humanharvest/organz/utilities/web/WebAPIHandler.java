package com.humanharvest.organz.utilities.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.utilities.CacheManager;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;

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

    public abstract List<String> getData(Object... arguments) throws IOException, BadDrugNameException, BadGatewayException;

    /**
     * Adds the given data to the cache, with the arguments as the key.
     * @param value The value to be stored in the cache.
     * @param arguments The key used to retrieve the cached value.
     * @return The value variable.
     */
    protected <T> T addCachedData(T value, Object... arguments) {
        Optional<Instant> expires = Optional.of(Instant.now().plus(Period.ofDays(7)));
        CacheManager.INSTANCE.addCachedData(getClass().getTypeName(), arguments, value, expires);
        return value;
    }

    /**
     * Retrieves the value from the cache, or an empty optional if it doesn't exist or is expired.
     * @param type The type of the value in the cache. Must be deserialisable using the GSON library.
     * @param arguments The key used to store the cached value.
     */
    protected <T> Optional<T> getCachedData(Type type, Object... arguments) {
        return CacheManager.INSTANCE.getCachedData(getClass().getTypeName(), type, arguments);
    }
}
