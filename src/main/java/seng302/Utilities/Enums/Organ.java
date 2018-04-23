package seng302.Utilities.Enums;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Enum for organs. Allows for to/from string conversion
 */
public enum Organ {
    LIVER("Liver"),
    KIDNEY("Kidney"),
    PANCREAS("Pancreas"),
    HEART("Heart"),
    LUNG("Lung"),
    INTESTINE("Intestine"),
    CORNEA("Cornea"),
    MIDDLE_EAR("Middle ear"),
    SKIN("Skin"),
    BONE("Bone"),
    BONE_MARROW("Bone marrow"),
    CONNECTIVE_TISSUE("Connective tissue");

    private final String text;
    public static final EnumSet<Organ> enumSet = EnumSet.allOf(Organ.class);

    Organ(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    /**
     * Get an Organ object from a string
     * @param text Text to convert
     * @return The matching organ
     * @throws IllegalArgumentException Thrown when no matching organ is found
     */
    public static Organ fromString(String text) {
        for (Organ o : Organ.values()) {
            if (o.toString().equalsIgnoreCase(text)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Unsupported organ");
    }

}
