package seng302;

import static org.junit.Assert.assertEquals;

import seng302.Utilities.Enums.Region;

import org.junit.Test;

public class ClinicianTest {
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
