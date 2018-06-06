package com.humanharvest.organz;

import static org.junit.jupiter.api.Assertions.*;

import com.humanharvest.organz.utilities.enums.Region;

import org.junit.jupiter.api.Test;

class ClinicianTest {
    @Test
    void getFullNameNoMiddleNameTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        assertEquals("First Last", clinician.getFullName());
    }

    @Test
    void getFullNameWithMiddleNameTest() {
        Clinician clinician = new Clinician("First", "Mid Name", "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        assertEquals("First Mid Name Last", clinician.getFullName());
    }
}
