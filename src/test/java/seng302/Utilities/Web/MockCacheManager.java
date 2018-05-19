package seng302.Utilities.Web;

import java.util.HashMap;

import seng302.Utilities.CacheManager;

public class MockCacheManager extends CacheManager {
    public MockCacheManager() {
        categories = new HashMap<>();
    }

    public boolean isEmpty() {
        return categories.isEmpty();
    }
}
