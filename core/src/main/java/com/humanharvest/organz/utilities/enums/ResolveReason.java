package com.humanharvest.organz.utilities.enums;

/**
 * Enum for resolve reasons. Allows for to/from string conversion
 */
public enum ResolveReason {
    ERROR("Input error"),
    COMPLETED("Transplant completed"),
    CURED("Disease was cured"),
    DECEASED("Client is deceased"),
    CUSTOM("Custom reason...");

    private final String text;

    private static String mismatchText;

    ResolveReason(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    public static ResolveReason fromString(String text) {
        for (ResolveReason r : ResolveReason.values()) {
            if (r.toString().equalsIgnoreCase(text)) {
                return r;
            }
        }

        //No match
        if (mismatchText != null) {
            throw new IllegalArgumentException(mismatchText);
        } else {
            StringBuilder mismatchTextBuilder = new StringBuilder("Unsupported resolve reason, please use one of the "
                    + "following:");
            for (ResolveReason rr : ResolveReason.values()) {
                mismatchTextBuilder.append("\n").append(rr.text);
            }
            mismatchText = mismatchTextBuilder.toString();
            throw new IllegalArgumentException(mismatchText);
        }
    }
}
