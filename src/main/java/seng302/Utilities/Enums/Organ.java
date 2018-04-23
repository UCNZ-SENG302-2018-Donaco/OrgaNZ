package seng302.Utilities.Enums;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

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
    public static List<Organ> organList = new ArrayList<>(Arrays.asList(Organ.values()));

    Organ(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    public static List<Organ> getOrganList() {
        Collections.sort(organList);
        return organList;
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
