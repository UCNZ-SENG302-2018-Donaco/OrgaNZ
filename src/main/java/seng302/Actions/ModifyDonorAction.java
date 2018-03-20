package seng302.Actions;

import seng302.Donor;

import java.beans.Statement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A reversible donor modification Action
 */
public class ModifyDonorAction implements Action {
    private static <T> Class<T> wrap(Class<T> c) {
        return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
    }

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>();

    private Map<String, Object> executors = new HashMap<>();
    private Map<String, Object> unExecutors = new HashMap<>();
    private Donor donor;
    private Method[] donorMethods;

    /**
     * Create a new Action
     * @param donor The donor to be modified
     */
    public ModifyDonorAction(Donor donor) {
        this.donor = donor;
        donorMethods = donor.getClass().getMethods();

        PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
        PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
        PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
        PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
        PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
        PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
        PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
    }

    /**
     * Add a modification to the donor
     * @param field The setter field of the donor. Must match a valid setter in the Donor object
     * @param oldValue The object the field initially had. Should be taken from the Donors equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     */
    public void addChange(String field, Object oldValue, Object newValue) throws NoSuchMethodException, NoSuchFieldException {
        for (Method m : donorMethods) {
            if (m.getName().equals(field)) {
                if (m.getParameterCount() != 1 || (oldValue != null && wrap(m.getParameterTypes()[0]) != oldValue.getClass()) || ( oldValue != null && oldValue.getClass() != newValue.getClass())) {
                    System.out.println(String.format("%s %s %s %s", m.getParameterCount(), m.getParameterTypes()[0], oldValue.getClass(), newValue.getClass()));
                    throw new NoSuchFieldException("Invalid fields");
                }
                executors.put(field, newValue);
                unExecutors.put(field, oldValue);
                return;
            }
        }
        throw new NoSuchMethodException("Donor does not contain that method");
    }

    private void checkMethod(String field, Object value1, Object value2) {

    }

    @Override
    public void execute() {
        runChanges(executors);
    }

    @Override
    public void unExecute() {
        runChanges(unExecutors);
    }

    /**
     * Loops through a map of String, Objects and applies the ava.beans.Statement to it using the String as the setter and the Object as the parameter
     * @param map A map of String, Objects
     */
    private void runChanges(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Object[] var = {entry.getValue()};
                Statement s = new Statement(donor, entry.getKey(), var);
                s.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
