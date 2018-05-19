package seng302.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;

public class CacheManager {
    public static final TypeAdapterFactory GSON_FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public TypeAdapter create(Gson gson, TypeToken type) {
            if (type.getRawType().equals(Category.class)) {
                return new TreeTypeAdapter(Category.GSON_ADAPTER, Category.GSON_ADAPTER, gson, type, this);
            }
            return null;
        }
    };

    public static CacheManager INSTANCE = new Impl();

    protected Map<String, Category> categories;

    protected CacheManager() {
    }

    public <T> void addCachedData(String categoryName, Object[] arguments, T value, Optional<Instant> expires) {
        Category category = categories.get(categoryName);
        if (category == null) {
            category = new Category();
            categories.put(categoryName, category);
        }

        Key key = new Key(arguments);
        Value realValue = new Value(JSONConverter.getGson().toJsonTree(value), expires);
        category.setData(key, realValue);
    }

    public <T> Optional<T> getCachedData(String categoryName, Type type, Object[] arguments) {
        Category category = categories.get(categoryName);
        if (category == null) {
            return Optional.empty();
        }

        return category.get(type, arguments);
    }

    private abstract static class JsonBaseSerialiser<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    }

    private static final class CategoryMap extends HashMap<String, Category> {

    }

    private static final class Category {
        public static final JsonBaseSerialiser<Category> GSON_ADAPTER = new CategorySerialiser();

        private final Map<Key, Value> values;

        public Category() {
            this(new HashMap<>());
        }

        Category(Map<Key, Value> values) {
            this.values = values;
        }

        public <T> Optional<T> get(Type type, Object[] arguments) {
            Key key = new Key(arguments);
            Value value = values.get(key);
            if (value == null) {
                return Optional.empty();
            }

            if (value.getExpires().isPresent()) {
                if (Instant.now().isAfter(value.getExpires().get())) {
                    return Optional.empty();
                }
            }

            return Optional.of(JSONConverter.getGson().fromJson(value.getValue(), type));
        }

        public void setData(Key key, Value value) {
            values.put(key, value);
        }

        public Map<Key, Value> getValues() {
            return Collections.unmodifiableMap(values);
        }

        private static class CategorySerialiser extends JsonBaseSerialiser<Category> {
            @Override
            public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                JsonArray array = json.getAsJsonArray();

                Map<Key, Value> values = new HashMap<>();

                for (JsonElement childElement : array) {
                    JsonObject object = childElement.getAsJsonObject();
                    Object[] key = context.deserialize(object.get("key"), Object[].class);
                    Key realKey = new Key(key);

                    JsonElement value = object.get("value");
                    JsonElement expires = object.get("expires");
                    Value realValue;
                    if (expires.isJsonNull()) {
                        realValue = new Value(value, Optional.empty());
                    } else {
                        Optional<Instant> realExpires = Optional.of(Instant.parse(expires.getAsString()));
                        realValue = new Value(value, realExpires);
                    }

                    values.put(realKey, realValue);
                }

                return new Category(values);
            }

            @Override
            public JsonElement serialize(Category src, Type typeOfSrc, JsonSerializationContext context) {
                JsonArray array = new JsonArray();
                for (Map.Entry<Key, Value> entry : src.getValues().entrySet()) {
                    Value value = entry.getValue();

                    JsonObject object = new JsonObject();
                    object.add("key", context.serialize(entry.getKey().getValue()));
                    object.add("value", context.serialize(value.getValue()));
                    if (value.getExpires().isPresent()) {
                        object.addProperty("expires", value.getExpires().get().toString());
                    }
                    array.add(object);
                }
                return array;
            }
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
        private final JsonElement value;
        private final Optional<Instant> expires;

        public Value(JsonElement value, Optional<Instant> expires) {
            this.value = value;
            this.expires = expires;
        }

        public JsonElement getValue() {
            return value;
        }

        public Optional<Instant> getExpires() {
            return expires;
        }
    }

    private static class Impl extends CacheManager {
        @Override
        public <T> void addCachedData(String categoryName, Object[] arguments, T value, Optional<Instant> expires) {
            lazyInitialise();
            super.addCachedData(categoryName, arguments, value, expires);
            saveData();
        }

        @Override
        public <T> Optional<T> getCachedData(String categoryName, Type type, Object[] arguments) {
            lazyInitialise();
            return super.getCachedData(categoryName, type, arguments);
        }

        private void saveData() {
            String result = JSONConverter.getGson().toJson(categories);

            String cacheDirectory = Config.getCacheDirectory();
            File cacheFile = Paths.get(cacheDirectory, "cache.json").toFile();

            try (OutputStream stream = new FileOutputStream(cacheFile)) {
                try (Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
                    writer.write(result);
                }
            } catch (IOException e) {
                // TODO: Log error
            }
        }

        private void lazyInitialise() {
            if (categories != null) {
                return;
            }

            String cacheDirectory = Config.getCacheDirectory();
            File cacheFile = Paths.get(cacheDirectory, "cache.json").toFile();

            try (InputStream stream = new FileInputStream(cacheFile)) {
                try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    categories = JSONConverter.getGson().fromJson(reader, CategoryMap.class);
                }
            } catch (JsonSyntaxException | FileNotFoundException e) {
                categories = new HashMap<>();
            } catch (IOException e) {
                // TODO: Log error
            }
        }
    }
}
