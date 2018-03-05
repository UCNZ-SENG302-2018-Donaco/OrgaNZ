package seng302.Utilities;

public enum Gender {
    MALE ("Male"),
    FEMALE ("Female"),
    OTHER ("Other"),
    UNSPECIFIED ("Unspecified");

    private final String text;

    Gender(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    public static Gender fromString(String text) {
        for (Gender g: Gender.values()) {
            if (g.toString().equalsIgnoreCase(text)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unsupported gender");
    }
}

