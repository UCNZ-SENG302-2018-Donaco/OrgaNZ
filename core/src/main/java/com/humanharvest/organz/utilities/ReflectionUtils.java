package com.humanharvest.organz.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

public abstract class ReflectionUtils {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ReflectionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> T getField(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    public static <T> void setField(Object o, String fieldName, T value) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    public static <T> void setStaticField(Class<?> clazz, String fieldName, T value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            removeFieldFinal(field);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    private static void removeFieldFinal(@SuppressWarnings("TypeMayBeWeakened") Field field) {
        if ((field.getModifiers() & Modifier.FINAL) != 0) {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ReflectionException(e);
            }
        }
    }

    public static <T> T invoke(Object o, String methodName, Object... parameters) {
        Method method = findMethod(o.getClass().getDeclaredMethods(), methodName, parameters.length);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        try {
            return (T) method.invoke(o, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    private static Method findMethod(Method[] methods, String name, int numberParameters) {
        return Arrays.stream(methods)
                .filter(method -> {
                    return Objects.equals(method.getName(), name) && method.getParameterCount() == numberParameters;
                }).findFirst()
                .orElse(null);

    }

    public static Field getFieldReference(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new ReflectionException(e);
        }
    }

    public static Method getMethodReference(Object o, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = findDeclaredMethod(o.getClass(), methodName, parameterTypes);
            Objects.requireNonNull(method);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }

    private static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchMethodException("Unable to find declared method " + methodName);
    }
}

