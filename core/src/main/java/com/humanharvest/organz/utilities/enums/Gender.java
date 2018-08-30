package com.humanharvest.organz.utilities.enums;

/**
 * Enum for genders. Allows for to/from string conversion
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    UNSPECIFIED("Unspecified");

    private static String mismatchText;

    private final String text;

    Gender(String text) {
        this.text = text;
    }

    /**
     * Get a Gender object from a string
     *
     * @param text Text to convert
     * @return The matching gender
     * @throws IllegalArgumentException Thrown when no matching gender is found
     */
    public static Gender fromString(String text) {
        for (Gender g : Gender.values()) {
            if (g.toString().equalsIgnoreCase(text)) {
                return g;
            }
        }

        //No match
        //We use a static text builder so this text is dynamically built based on the ENUM options, but only needs to
        // be built once per runtime
        if (mismatchText != null) {
            throw new IllegalArgumentException(mismatchText);
        } else {
            StringBuilder mismatchTextBuilder = new StringBuilder("Unsupported gender, please use one of the "
                    + "following:");
            for (Gender g : Gender.values()) {
                mismatchTextBuilder.append('\n').append(g.text);
            }
            mismatchText = mismatchTextBuilder.toString();
            throw new IllegalArgumentException(mismatchText);
        }
    }

    public String toString() {
        return text;
    }
}

