package com.humanharvest.organz.actions;

import java.lang.reflect.Field;

import com.humanharvest.organz.utilities.type_converters.PrimitiveConverter;

/**
 * Create a new modification on any object using it's field name and the old and new values
 * Exceptions are thrown if the object does not contain that field, or if the values are the wrong type
 * If you have a field such as a password, use the isPrivate boolean to ensure the values are not leaked
 */
public class ModifyObjectByFieldAction extends Action {

    private Object toModify;
    private Field field;
    private Object oldValue;
    private Object newValue;
    private boolean isPrivate = false;

    /**
     * Create a new modification
     * @param toModify The object to be modified
     * @param field The setter field of the object. Must match a valid setter
     * @param oldValue The object the field initially had. Should be taken from the objects equivalent getter
     * @param newValue The object the field should be update to. Must match the setters object type
     * @throws NoSuchFieldException Thrown if the object setter expected type does not match one of the value types
     */
    public ModifyObjectByFieldAction(Object toModify, String field, Object oldValue, Object newValue)
            throws NoSuchFieldException {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.toModify = toModify;
        setField(field);
        checkTypes();
    }

    /**
     * Create a new modification with the option to hide the values
     * @param toModify The object to be modified
     * @param field The setter field of the object. Must match a valid setter
     * @param oldValue The object the field initially had. Should be taken from the objects equivalent getter
     * @param newValue The object the field should be update to. Must match the setters object type
     * @param isPrivate If set to true, the text returns will only return the field, not the values
     * @throws NoSuchFieldException Thrown if the object setter expected type does not match one of the value types
     */
    public ModifyObjectByFieldAction(Object toModify, String field, Object oldValue, Object newValue, boolean isPrivate)
            throws NoSuchFieldException {
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.isPrivate = isPrivate;
        this.toModify = toModify;
        setField(field);
        checkTypes();
    }

    /**
     * Create a new modification
     * @param toModify The object to be modified
     * @param field The setter field of the object. Must match a valid field
     * @param newValue The object the field should be update to. Must match the setters object type
     * @throws NoSuchFieldException Thrown if the object setter expected type does not match one of the value types
     */
    public ModifyObjectByFieldAction(Object toModify, String field, Object newValue)
            throws NoSuchFieldException {
        this.newValue = newValue;
        this.toModify = toModify;
        setField(field);
        setOldValue();
        checkTypes();
    }

    private void setOldValue() throws NoSuchFieldException {
        try {
            oldValue = this.field.get(toModify);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new NoSuchFieldException("Something went wrong");
        }
    }

    private void setField(String field)
            throws NoSuchFieldException {
        this.field = toModify.getClass().getDeclaredField(field);
        if (this.field == null) {
            throw new NoSuchFieldException("Field does not exist in that object");
        }
        this.field.setAccessible(true);
    }

    private void checkIsInView() {

    }

    private void checkTypes() throws NoSuchFieldException {
        PrimitiveConverter converter = new PrimitiveConverter();
        Class<?> expectedClass = converter.convertToWrapper(field.getType());
        if ((newValue != null && newValue.getClass() != expectedClass) || (oldValue != null
                && oldValue.getClass() != expectedClass)) {
            throw new NoSuchFieldException("Field expects a different field type than the one given");
        }
    }

    @Override
    public void execute() {
        runChange(newValue);
    }

    @Override
    public void unExecute() {
        runChange(oldValue);
    }

    private String unCamelCase(String inCamelCase) {
        String unCamelCased = inCamelCase.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        return unCamelCased.substring(0, 1).toUpperCase() + unCamelCased.substring(1);
    }

    private String formatValue(Object value) {
        return value != null ? String.format("'%s'", value.toString()) : "empty";
    }

    @Override
    public String getExecuteText() {
        if (isPrivate) {
            return unCamelCase(field.getName()) + ".";
        } else {
            return String.format("%s from %s to %s.", unCamelCase(field.getName()), formatValue(oldValue),
                    formatValue(newValue));
        }
    }

    @Override
    public String getUnexecuteText() {
        if (isPrivate) {
            return unCamelCase(field.getName()) + ".";
        } else {
            return String.format("%s from %s to %s.", unCamelCase(field.getName()), formatValue(newValue),
                    formatValue(oldValue));
        }
    }

    @Override
    public Object getModifiedObject() {
        return toModify;
    }

    /**
     * Execute a statement update on the object. Should not throw the errors from Statement as we check them in the
     * constructor
     * @param value Value to set
     */
    private void runChange(Object value) {
        try {
            field.set(toModify, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
