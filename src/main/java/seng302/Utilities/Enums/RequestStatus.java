package seng302.Utilities.Enums;

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
