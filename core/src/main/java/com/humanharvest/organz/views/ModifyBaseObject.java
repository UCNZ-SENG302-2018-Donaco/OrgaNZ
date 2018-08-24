package com.humanharvest.organz.views;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModifyBaseObject {

    public static class Serialiser<T extends ModifyBaseObject> extends StdSerializer<T> {

        public Serialiser() {
            this(null);
        }

        public Serialiser(Class<T> t) {
            super(t);
        }

        @Override
        public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {
            jsonGenerator.writeStartObject();
            for (Field field : t.getModifiedFields()) {
                try {
                    field.setAccessible(true);
                    jsonGenerator.writeObjectField(field.getName(), field.get(t));
                } catch (IllegalAccessException e) {
                    throw new IOException("Attempted to access an invalid field", e);
                }
            }
            jsonGenerator.writeEndObject();
        }
    }

    @JsonIgnore
    protected final Set<Field> modifiedFields = new HashSet<>();

    @JsonIgnore
    public Set<Field> getModifiedFields() {
        return Collections.unmodifiableSet(modifiedFields);
    }

    @JsonIgnore
    public String[] getUnmodifiedFields() {
        Collection<Field> allFields = new ArrayList<>(Arrays.asList(getClass().getDeclaredFields()));
        allFields.removeAll(modifiedFields);
        return allFields.stream().map(Field::getName).toArray(String[]::new);
    }

    @JsonIgnore
    public void registerChange(String fieldName) {
        try {
            modifiedFields.add(getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Invalid field", e);
        }
    }

    @JsonIgnore
    public void deregisterChange(String fieldName) {
        try {
            modifiedFields.remove(getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Invalid field", e);
        }
    }
}
