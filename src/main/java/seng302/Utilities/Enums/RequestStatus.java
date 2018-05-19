package seng302.Utilities.Enums;

/**
 * Enum for Request statuses. Allows for to/from string conversion
 */
public enum RequestStatus {
    WAITING,
    CANCELLED,
    COMPLETED;

    public static RequestStatus fromString(String text) {
        for (RequestStatus rs : RequestStatus.values()) {
            if (rs.toString().equalsIgnoreCase(text)) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Invalid request status.");
    }
}
