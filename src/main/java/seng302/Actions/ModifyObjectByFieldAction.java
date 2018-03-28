package seng302.Actions;

import seng302.Utilities.TypeConverters.PrimitiveConverter;

import java.beans.Statement;
import java.lang.reflect.Method;

public class ModifyObjectByFieldAction implements Action {

    private Object toModify;
    private String field;
    private Object[] oldValue;
    private Object[] newValue;

    /**
     * Create a new modification
     * @param toModify The object to be modified
     * @param field The setter field of the object. Must match a valid setter
     * @param oldValue The object the field initially had. Should be taken from the objects equivalent getter
     * @param newValue The object the field should be update to. Must match the setters object type
     * @throws NoSuchMethodException Thrown if the object does not have the field specified
     * @throws NoSuchFieldException Thrown if the object setter expected type does not match one of the value types
     */
    public ModifyObjectByFieldAction(Object toModify, String field, Object oldValue, Object newValue) throws NoSuchMethodException, NoSuchFieldException {
        Method[] objectMethods = toModify.getClass().getMethods();
        for (Method method : objectMethods) {
            if (method.getName().equals(field)) {
                if (method.getParameterCount() != 1) {
                    throw new NoSuchFieldException("Method expects more than one field");
                }
                PrimitiveConverter converter = new PrimitiveConverter();
                Class<?> expectedClass = converter.convertToWrapper(method.getParameterTypes()[0]);
                if ((newValue != null && newValue.getClass() != expectedClass) || (oldValue != null && oldValue.getClass() != expectedClass)) {
                    throw new NoSuchFieldException("Method expects a different field type than the one given");
                }
                this.toModify = toModify;
                this.field = field;
                this.oldValue = new Object[]{oldValue};
                this.newValue = new Object[]{newValue};
                return;
            }
        }
        throw new NoSuchMethodException("Donor does not contain that method");
    }

    @Override
    public void execute() {
        runChange(newValue);
    }

    @Override
    public void unExecute() {
        runChange(oldValue);
    }

    /**
     * Execute a statement update on the object. Should not throw the errors from Statement as we check them in the constructor
     * @param value Value to set
     */
    private void runChange(Object[] value) {
        try {
            Statement statement = new Statement(toModify, field, value);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
