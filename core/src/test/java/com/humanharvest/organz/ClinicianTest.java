package com.humanharvest.organz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ClinicianTest {

    @Test
    void getFullNameNoMiddleNameTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", "UNSPECIFIED", null, 1, "pass");
        assertEquals("First Last", clinician.getFullName());
    }

    @Test
    void getFullNameWithMiddleNameTest() {
        Clinician clinician = new Clinician("First", "Mid Name", "Last", "Address", "UNSPECIFIED", null, 1, "pass");
        assertEquals("First Mid Name Last", clinician.getFullName());
    }
}
