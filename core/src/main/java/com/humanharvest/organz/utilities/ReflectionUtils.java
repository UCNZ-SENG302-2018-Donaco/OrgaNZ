package com.humanharvest.organz.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static <T> T getField(Object o, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(o);
    }

    public static <T> void setField(Object o, String fieldName, T value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, value);
    }

    public static <T> void setStaticField(Class<?> clazz, String fieldName, T value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        removeFieldFinal(field);
        field.set(null, value);
    }

    private static void removeFieldFinal(@SuppressWarnings("TypeMayBeWeakened") Field field) throws NoSuchFieldException, IllegalAccessException {
        if ((field.getModifiers() & Modifier.FINAL) != 0) {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
    }

    public static <T> T invoke(Object o, String methodName, Object... parameters)
            throws InvocationTargetException, IllegalAccessException {
        Method method = findMethod(o.getClass().getDeclaredMethods(), methodName, parameters.length);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        return (T) method.invoke(o, parameters);
    }

    private static Method findMethod(Method[] methods, String name, int numberParameters) {
        return Arrays.stream(methods)
            .filter(method -> {
                return Objects.equals(method.getName(), name) && method.getParameterCount() == numberParameters;
            }).findFirst()
            .orElse(null);

    }

    public static Field getFieldReference(Object o, String fieldName) throws NoSuchFieldException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Method getMethodReference(Object o, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = findDeclaredMethod(o.getClass(), methodName, parameterTypes);
        Objects.requireNonNull(method);
        method.setAccessible(true);
        return method;
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
