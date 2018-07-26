package com.humanharvest.organz.utilities.web;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.humanharvest.organz.utilities.CacheManager;
import com.humanharvest.organz.utilities.JSONConverter;

public class MockCacheManager extends CacheManager {
    public MockCacheManager() {
        categories = new HashMap<>();
    }

    public static MockCacheManager Create() {
        MockCacheManager cacheManager = new MockCacheManager();

        // This ugly piece of code sets the CacheManager.INSTANCE variable to our mocked one.
        try {
            Field field = CacheManager.class.getField("INSTANCE");
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null, cacheManager);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new UnsupportedOperationException(e);
        }

        return cacheManager;
    }

    public boolean isEmpty() {
        return categories.isEmpty();
    }

    public String save() throws IOException {
        StringWriter writer = new StringWriter();
        JSONConverter.getObjectMapper().writeValue(writer, categories);
        return writer.toString();
    }

    public void load(String value) throws IOException {
        categories = JSONConverter.getObjectMapper().readValue(value,
                new TypeReference<Map<String, CacheManager.Category>>(){
                });
    }
}
