package com.humanharvest.organz;

import static org.junit.Assert.assertEquals;

import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Test;

public class ClinicianTest extends BaseTest {
    @Test
    public void getFullNameNoMiddleNameTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        assertEquals("First Last", clinician.getFullName());
    }

    @Test
    public void getFullNameWithMiddleNameTest() {
        Clinician clinician = new Clinician("First", "Mid Name", "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        assertEquals("First Mid Name Last", clinician.getFullName());
    }
}
