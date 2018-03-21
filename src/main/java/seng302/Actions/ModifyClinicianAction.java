package seng302.Actions;

import seng302.Clinician;
import seng302.Donor;
import seng302.Utilities.PrimitiveConverter;

import java.beans.Statement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A reversible clinician modification Action
 */
public class ModifyClinicianAction implements Action {
    private Map<String, Object> executors = new HashMap<>();
    private Map<String, Object> unExecutors = new HashMap<>();
    private Clinician clinician;
    private Method[] clinicianMethods;

    /**
     * Create a new Action
     * @param clinician The clinician to be modified
     */
    public ModifyClinicianAction(Clinician clinician) {
        this.clinician = clinician;
        clinicianMethods = clinician.getClass().getMethods();
    }

    /**
     * Add a modification to the clinician
     * @param field The setter field of the clinician. Must match a valid setter in the Clinician object
     * @param oldValue The object the field initially had. Should be taken from the Clinicians equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     */
    public void addChange(String field, Object oldValue, Object newValue) throws NoSuchMethodException, NoSuchFieldException {
        for (Method m : clinicianMethods) {
            if (m.getName().equals(field)) {
                if (m.getParameterCount() != 1) {
                    throw new NoSuchFieldException("Method expects more than one field");
                }
                PrimitiveConverter converter = new PrimitiveConverter();
                Class<?> expectedClass = converter.convertToWrapper(m.getParameterTypes()[0]);
                if ((newValue != null && newValue.getClass() != expectedClass) || (oldValue != null && oldValue.getClass() != expectedClass)) {
                    throw new NoSuchFieldException("Method expects a different field type than the one given");
                }
                executors.put(field, newValue);
                unExecutors.put(field, oldValue);
                return;
            }
        }
        throw new NoSuchMethodException("Donor does not contain that method");
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
     * Loops through a map of String, Objects and applies the Java.beans.Statement to it using the String as the setter and the Object as the parameter
     * @param map A map of String, Objects
     */
    private void runChanges(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Object[] var = {entry.getValue()};
                Statement s = new Statement(clinician, entry.getKey(), var);
                s.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
