package com.humanharvest.organz.utilities.type_converters;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveConverter {

    /**
     * Returns an object converted from it's respective primitive. If already an object just returns it
     *
     * @param toConvert The primitive to convert
     * @return The converted primitive or the initial object if not primitive
     */
    public Class<?> convertToWrapper(Class<?> toConvert) {
        if (!toConvert.isPrimitive()) {
            return toConvert;
        } else {
            Map<Class<?>, Class<?>> primitivesToWrappers = new HashMap<>();
            primitivesToWrappers.put(boolean.class, Boolean.class);
            primitivesToWrappers.put(byte.class, Byte.class);
            primitivesToWrappers.put(char.class, Character.class);
            primitivesToWrappers.put(double.class, Double.class);
            primitivesToWrappers.put(float.class, Float.class);
            primitivesToWrappers.put(int.class, Integer.class);
            primitivesToWrappers.put(long.class, Long.class);
            primitivesToWrappers.put(short.class, Short.class);
            primitivesToWrappers.put(void.class, Void.class);
            return primitivesToWrappers.get(toConvert);
        }
    }
}
