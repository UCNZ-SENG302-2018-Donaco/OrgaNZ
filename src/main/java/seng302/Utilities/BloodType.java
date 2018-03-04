package seng302;

public enum BloodType {
    A_NEG ("A-"),
    A_POS ("A+"),
    AB_NEG ("AB-"),
    AB_POS ("AB+"),
    B_NEG ("B-"),
    B_POS ("B+"),
    O_NEG ("O-"),
    O_POS ("O+");

    private final String text;

    BloodType(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    public static BloodType fromString(String text) {
        for (BloodType b : BloodType.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No blood type of type " + text + "found");
    }
}
