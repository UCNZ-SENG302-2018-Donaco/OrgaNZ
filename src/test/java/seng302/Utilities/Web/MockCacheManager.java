package seng302.Utilities.Web;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import seng302.Utilities.CacheManager;

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
}
