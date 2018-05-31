package com.humanharvest.organz.utilities.enums;

/**
 * Enum for blood types. Allows for to/from string conversion
 */
public enum BloodType {
    A_NEG("A-"),
    A_POS("A+"),
    AB_NEG("AB-"),
    AB_POS("AB+"),
    B_NEG("B-"),
    B_POS("B+"),
    O_NEG("O-"),
    O_POS("O+");

    private final String text;

    private static String mismatchText;

    BloodType(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    /**
     * Get a BloodType object from a string
     * @param text Text to convert, in the form A-
     * @return The matching bloodtype
     * @throws IllegalArgumentException Thrown when no matching bloodtype is found
     */
    public static BloodType fromString(String text) {
        for (BloodType bloodType : BloodType.values()) {
            if (bloodType.toString().equalsIgnoreCase(text)) {
                return bloodType;
            }
        }

        //No match
        if (mismatchText != null) {
            throw new IllegalArgumentException(mismatchText);
        } else {
            StringBuilder mismatchTextBuilder = new StringBuilder("Unsupported blood type, please use one of the "
                    + "following:");
            for (BloodType bloodType : BloodType.values()) {
                mismatchTextBuilder.append('\n').append(bloodType.text);
            }
            mismatchText = mismatchTextBuilder.toString();
            throw new IllegalArgumentException(mismatchText);
        }
    }
}
