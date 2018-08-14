package com.humanharvest.organz.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static <T> T getField(Object o, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T)field.get(o);
    }

    public static <T> void setField(Object o, String fieldName, T value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, value);
    }

    public static <T> T invoke(Object o, String methodName, Object... parameters)
            throws InvocationTargetException, IllegalAccessException {
        Method method = findMethod(o.getClass().getDeclaredMethods(), methodName, parameters.length);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        return (T)method.invoke(o, parameters);
    }

    private static Method findMethod(Method[] methods, String name, int numberParameters) {
        for (Method method : methods) {
            if (Objects.equals(method.getName(), name) && method.getParameterCount() == numberParameters) {
                return method;
            }
        }

        return null;
    }

    public static Field getFieldReference(Object o, String fieldName) throws NoSuchFieldException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Method getMethodReference(Object o, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = o.getClass().getDeclaredMethod(methodName, parameterTypes);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        return method;
    }
}
