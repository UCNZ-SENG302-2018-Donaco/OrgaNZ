package seng302;

import static org.junit.Assert.assertEquals;

import seng302.Utilities.Enums.Region;

import org.junit.Test;

public class AdministratorTest {
    @Test
    public void getFullNameNoMiddleNameTest() {
        Administrator administrator = new Administrator("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        assertEquals("First Last", administrator.getFullName());
    }

    @Test
    public void getFullNameWithMiddleNameTest() {
        Administrator administrator = new Administrator("First", "Mid Name", "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        assertEquals("First Mid Name Last", administrator.getFullName());
    }
}
