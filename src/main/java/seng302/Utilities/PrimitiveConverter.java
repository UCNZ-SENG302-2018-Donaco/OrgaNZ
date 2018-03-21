package seng302.Utilities;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveConverter {

    /**
     * Returns an object converted from it's respective primitive. If already an object just returns it
     * @param toConvert The primitive to convert
     * @return The converted primitive or the initial object if not primitive
     */
    public Class<?> convertToWrapper(Class<?> toConvert) {
        if (!toConvert.isPrimitive()) {
            return toConvert;
        } else {
            Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<>();
            PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
            PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
            PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
            PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
            PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
            PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
            PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
            PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
            PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
            return PRIMITIVES_TO_WRAPPERS.get(toConvert);
        }
    }
}
