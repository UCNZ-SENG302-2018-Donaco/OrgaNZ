package com.humanharvest.organz.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;
import com.humanharvest.organz.utilities.web.DrugInteractionsHandler;
import com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler;
import com.humanharvest.organz.utilities.web.WebAPIHandler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.http.HttpTransport;

/**
 * Stores and retrieves arbitrary values from a persistent location.
 */
public abstract class CacheManager {

    /**
     * The instance of the cache.
     */
    public static final CacheManager INSTANCE = new Impl();

    private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());

    /**
     * A map of category names to categories.
     * E.g.:
     * Category name: DrugInteractionsHandler
     * Category: Has a variable `values`, which is a Map(Key, Value)
     * Key: Has a variable `value`, which is an Object[] (used to store medication name/s)
     * Value: Has a variable value, which is a JsonElement, and a variable expires, which is an Optional(Instant)
     * (used to store ingredients/interactions, and expiry datetime)
     */
    protected Map<String, Category> categories;

    protected CacheManager() {
    }

    /**
     * Adds data to a cache, given a category, key, value and optional expiry date.
     *
     * @param categoryName The category of the cached value.
     * @param arguments The key used to store/retrieve the cached value.
     * @param value The cached value.
     * @param expires An optional expiry date.
     */
    public <T> void addCachedData(String categoryName, Object[] arguments, T value, Optional<Instant> expires) {
        if (Objects.isNull(arguments) || arguments.length == 0) {
            throw new IllegalArgumentException("arguments must contain at least one value");
        }
        if (Objects.isNull(categoryName)) {
            throw new IllegalArgumentException("categoryName must not be null");
        }
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("value must not be null");
        }

        Category category = categories.get(categoryName);
        if (category == null) {
            category = new Category();
            categories.put(categoryName, category);
        }

        Key key = new Key(arguments);
        Value realValue =
                new Value(JSONConverter.getObjectMapper().convertValue(value, JsonNode.class),
                        expires);
        category.setData(key, realValue);
    }

    /**
     * Gets data from a cache, given a category and key.
     *
     * @param categoryName The category of the cached value.
     * @param type The type of the cached value, must be deserialisable using GSON.
     * @param arguments The key used to store/retrieve the cached value.
     * @return The stored data, or an empty optional if it cannot be found or has expired.
     */
    public <T> Optional<T> getCachedData(String categoryName, TypeReference<?> type, Object[] arguments) {
        if (Objects.isNull(arguments) || arguments.length == 0) {
            throw new IllegalArgumentException("arguments must contain at least one value");
        }
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (Objects.isNull(categoryName)) {
            throw new IllegalArgumentException("categoryName must not be null");
        }

        Category category = categories.get(categoryName);
        if (category == null) {
            // Not cached
            return Optional.empty();
        }

        return category.get(type, arguments);
    }

    private void removeCachedData(String categoryName, Object[] arguments) {
        if (Objects.isNull(arguments) || arguments.length == 0) {
            throw new IllegalArgumentException("arguments must contain at least one value");
        }
        if (Objects.isNull(categoryName)) {
            throw new IllegalArgumentException("categoryName must not be null");
        }

        Category category = categories.get(categoryName);
        if (category != null) {
            category.remove(arguments);
        }
    }

    public void refreshCachedData() {
        refreshCachedData(null);
    }

    public void refreshCachedData(HttpTransport httpTransport) {
        // Iterate through categories
        for (Map.Entry<String, CacheManager.Category> pair : categories.entrySet()) {
            String categoryName = pair.getKey();
            Category category = pair.getValue();

            WebAPIHandler handler;
            switch (categoryName) {
                case "com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler":
                    handler = httpTransport == null ?
                            new MedActiveIngredientsHandler() :
                            new MedActiveIngredientsHandler(httpTransport);
                    break;
                case "com.humanharvest.organz.utilities.web.DrugInteractionsHandler":
                    handler = httpTransport == null ?
                            new DrugInteractionsHandler() :
                            new DrugInteractionsHandler(httpTransport);
                    break;
                default:
                    LOGGER.log(Level.SEVERE, "Unrecognised handler: " + categoryName);
                    handler = null;
                    break;
            }

            if (handler != null) {
                // Store a list of the keys' values currently in the category
                Collection<Key> keys = new ArrayList<>(category.getValues().keySet());

                for (Key key : keys) {
                    Object[] rawKey = key.getValue();
                    if (rawKey != null) {
                        try {
                            removeCachedData(categoryName, rawKey);
                            handler.getData(rawKey);
                        } catch (BadDrugNameException | BadGatewayException | IOException e) {
                            LOGGER.log(Level.WARNING, "Couldn't refresh " + rawKey[0] + " in cache.", e);
                        }
                    }
                }
            }
        }
    }

    @JsonSerialize(using = CategorySerialiser.class)
    @JsonDeserialize(using = CategoryDeserialiser.class)
    protected static final class Category {

        private final Map<Key, Value> values;

        public Category() {
            this(new HashMap<>());
        }

        Category(Map<Key, Value> values) {
            this.values = values;
        }

        public <T> Optional<T> get(TypeReference<?> type, Object[] arguments) {
            Key key = new Key(arguments);
            Value value = values.get(key);
            if (value == null) {
                return Optional.empty();
            }

            Optional<Instant> expires = value.getExpires();
            if (expires.isPresent()) {
                if (Instant.now().isAfter(expires.get())) {
                    return Optional.empty();
                }
            }

            return Optional.of(JSONConverter.getObjectMapper().convertValue(value.getValue(), type));
        }

        public void remove(Object[] arguments) {
            Key key = new Key(arguments);
            values.remove(key);
        }

        public void setData(Key key, Value value) {
            values.put(key, value);
        }

        public Map<Key, Value> getValues() {
            return Collections.unmodifiableMap(values);
        }

        /**
         * Removes all the values from the category.
         */
        public void clearValues() {
            values.clear();
        }
    }

    private static final class CategorySerialiser extends JsonSerializer<Category> {

        @Override
        public void serialize(Category value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            for (Map.Entry<Key, Value> entries : value.getValues().entrySet()) {
                gen.writeStartObject();
                gen.writeObjectField("key", entries.getKey().getValue());
                Optional<Instant> expires = entries.getValue().getExpires();
                if (expires.isPresent()) {
                    gen.writeObjectField("expires", expires.get());
                }
                gen.writeObjectField("value", entries.getValue().getValue());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    private static final class CategoryDeserialiser extends StdDeserializer<Category> {

        protected CategoryDeserialiser() {
            super(Category.class);
        }

        @Override
        public Category deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectNode[] nodes = p.readValueAs(ObjectNode[].class);

            Category category = new Category();
            for (ObjectNode node : nodes) {
                JsonNode keyNode = node.get("key");
                JsonNode expiresNode = node.get("expires");
                JsonNode valueNode = node.get("value");

                if (keyNode == null || valueNode == null) {
                    throw new JsonParseException(p, "Object nodes ned a key and value key");
                }

                Object[] keys = JSONConverter.getObjectMapper().convertValue(keyNode, Object[].class);
                Optional<Instant> expires = Optional.empty();
                if (expiresNode != null) {
                    expires = Optional.of(JSONConverter.getObjectMapper().convertValue(expiresNode, Instant.class));
                }
                category.setData(new Key(keys), new Value(valueNode, expires));
            }
            return category;
        }
    }

    private static final class Key {

        private final Object[] value;

        public Key(Object[] value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Key key = (Key) obj;
            return Arrays.equals(value, key.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }

        public Object[] getValue() {
            return value;
        }
    }

    private static final class Value {

        private final JsonNode value;
        private final Optional<Instant> expires;

        public Value(JsonNode value, Optional<Instant> expires) {
            this.value = value;
            this.expires = expires;
        }

        public JsonNode getValue() {
            return value;
        }

        public Optional<Instant> getExpires() {
            return expires;
        }
    }

    /**
     * Default implementation of CacheManager, stores in a json file in the user cache directory.
     */
    private static class Impl extends CacheManager {

        @Override
        public <T> void addCachedData(String categoryName, Object[] arguments, T value, Optional<Instant> expires) {
            lazyInitialise();
            super.addCachedData(categoryName, arguments, value, expires);
            saveData();
        }

        @Override
        public <T> Optional<T> getCachedData(String categoryName, TypeReference<?> type, Object[] arguments) {
            lazyInitialise();
            return super.getCachedData(categoryName, type, arguments);
        }

        @Override
        public void refreshCachedData() {
            lazyInitialise();
            super.refreshCachedData();
            saveData();
        }

        /**
         * Saves data into a cache file.
         */
        private void saveData() {
            String cacheDirectory = Config.getCacheDirectory();
            File cacheFile = Paths.get(cacheDirectory, "cache.json").toFile();
            try {
                JSONConverter.getObjectMapper().writeValue(cacheFile, categories);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to save cache data", e);
            }
        }

        /**
         * Loads data from a cached file, if not already done.
         */
        private void lazyInitialise() {
            if (categories != null) {
                // Data has already been loaded from cached file
                return;
            }

            String cacheDirectory = Config.getCacheDirectory();
            File cacheFile = Paths.get(cacheDirectory, "cache.json").toFile();

            try {
                categories = JSONConverter.getObjectMapper().readValue(cacheFile,
                        new TypeReference<Map<String, Category>>() {
                        });
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
                categories = new HashMap<>();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to load cache data", e);
                categories = new HashMap<>();
            }
        }
    }
}
