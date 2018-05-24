package seng302.Utilities.Enums;

/**
 * Enum for Request statuses. Allows for to/from string conversion
 */
public enum TransplantRequestStatus {
    WAITING,
    CANCELLED,
    COMPLETED;

    public static TransplantRequestStatus fromString(String text) {
        for (TransplantRequestStatus rs : TransplantRequestStatus.values()) {
            if (rs.toString().equalsIgnoreCase(text)) {
                return rs;
            }
        }
        throw new IllegalArgumentException("Invalid request status.");
    }
}
