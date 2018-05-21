package seng302.Utilities.Enums;

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
        throw new IllegalArgumentException("Unsupported resolve reason");
    }
}
