package com.humanharvest.organz.utilities.enums;

public enum ClientType {

    ANY("All"),
    ONLY_DONOR("Exclusively donors"),
    ONLY_RECEIVER("Exclusively receivers"),
    NEITHER("Neither donors nor receivers"),
    BOTH("Both donors and receivers");

    private final String description;

    ClientType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
