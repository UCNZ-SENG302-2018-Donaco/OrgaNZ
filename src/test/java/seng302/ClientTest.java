package seng302;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;

import java.util.HashSet;

import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;

public class ClientTest {

    private Client client;

    @Before
    public void createClient() {
        client = new Client(1);
    }

    @Test
    public void getBMI1Test() {
        client.setWeight(70);
        client.setHeight(180);
        assertEquals(21.6, client.getBMI(), 0.01);
    }

    @Test
    public void getBMI2Test() {
        client.setWeight(0);
        client.setHeight(180);
        assertEquals(0, client.getBMI(), 0.0);
    }

    @Test
    public void getAgeTest() {
        LocalDate dob = LocalDate.of(2000, 1, 1);
        int age = Period.between(dob, LocalDate.now()).getYears();
        client.setDateOfBirth(dob);
        assertEquals(client.getAge(), age);

    }

    @Test
    public void getAge2Test() {
        LocalDate dob = LocalDate.of(2000, 1, 1);
        LocalDate dod = LocalDate.of(2010, 1, 1);
        client.setDateOfBirth(dob);
        client.setDateOfDeath(dod);
        assertEquals(10, client.getAge());
    }

    @Test
    public void CheckNameContainsValidTest() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("First"));
    }

    @Test
    public void CheckNameContainsCaseInsensitivityValidTest() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("first"));
    }

    @Test
    public void CheckNameContainsLastNameValidTest() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("La"));
    }

    @Test
    public void CheckNameContainsMiddleNameValidTest() {
        client = new Client("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("mid"));
    }

    @Test
    public void CheckNameContainsPreferredNameValidTest() {
        client = new Client("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Preferred");
        assertTrue(client.nameContains("Preferred"));
    }

    @Test
    public void CheckNameContainsNotValidTest() {
        client = new Client("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.nameContains("notin"));
    }

    @Test
    public void CheckNameContainsMultipleChecksValidTest() {
        client = new Client("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("F Last"));
    }

    @Test
    public void CheckNameContainsMultipleChecksOneInvalidTest() {
        client = new Client("First", "middlename", "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.nameContains("F mid not"));
    }

    @Test
    public void GetFullNameNoMiddleNameNoPreferredNameTest() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertEquals("First Last", client.getFullName());
    }

    @Test
    public void GetFullNameWithMiddleNameNoPreferredNameTest() {
        client = new Client("First", "Mid Name", "Last", LocalDate.of(1970, 1, 1), 1);
        assertEquals("First Mid Name Last", client.getFullName());
    }

    @Test
    public void GetFullNameNoMiddleNameWithPreferredNameTest() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Pref");
        assertEquals("First \"Pref\" Last", client.getFullName());
    }

    @Test
    public void GetFullNameWithMiddleNameWithPreferredNameTest() {
        client = new Client("First", "Mid Name", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Pref");
        assertEquals("First Mid Name \"Pref\" Last", client.getFullName());
    }

    /*
    @Test
    public void noRequestCurrentOrganRequest1Test() {
        assertEquals(false, client.isReceiver());
    }

    @Test
    public void noRequestCurrentOrganRequest2Test() {
        Organ o = Organ.HEART;
        TransplantRequest t = new TransplantRequest(o, false);
        client.addTransplantRequest(t);
        assertEquals(false, client.isReceiver());
    }


    @Test
    public void validCurrentOrganRequestTest() {
        Organ o = Organ.HEART;
        TransplantRequest t = new TransplantRequest(o, true);
        client.addTransplantRequest(t);
        assertEquals(true, client.isReceiver());
    }

    @Test
    public void getOrganStatusStringEmpty1Test() {
        assertEquals("No organs found", client.getOrganStatusString("requests"));
    }

    @Test
    public void getOrganStatusStringEmpty2Test() {
        assertEquals("No organs found", client.getOrganStatusString("donations"));
    }

    @Test
    public void getOrganStatusStringInvalidStringTest() {
        assertEquals("Invalid input", client.getOrganStatusString(""));
    }

    @Test
    public void getOrganStatusStringValid1Test() {
        Organ o = Organ.HEART;
        try {
            client.setOrganRequestStatus(o, true);
        } catch (OrganAlreadyRegisteredException ex) {
            System.out.println(ex);
        }
        assertEquals("Heart", client.getOrganStatusString("requests"));
    }

    @Test
    public void getOrganStatusStringValid2Test() {
        Organ o = Organ.HEART;
        try {
            client.setOrganDonationStatus(o, true);
        } catch (OrganAlreadyRegisteredException ex) {
            System.out.println(ex);
        }
        assertEquals("Heart", client.getOrganStatusString("donations"));
    }

    @Test
    public void getOrganStatusStringValid3Test() {
        try {
            client.setOrganRequestStatus(Organ.BONE, true);
            client.setOrganRequestStatus(Organ.HEART, true);
            client.setOrganRequestStatus(Organ.LIVER, true);
        } catch (OrganAlreadyRegisteredException ex) {
            fail(ex.getMessage());
        }
        String organStatusString = client.getOrganStatusString("requests");
        assertTrue(organStatusString.contains("Bone"));
        assertTrue(organStatusString.contains("Heart"));
        assertTrue(organStatusString.contains("Liver"));
    }

    @Test(expected = OrganAlreadyRegisteredException.class)
    public void getOrganStatusStringInvalidTest() throws OrganAlreadyRegisteredException {
        client.setOrganRequestStatus(Organ.BONE, true);
        client.setOrganRequestStatus(Organ.BONE, true);
    }
    */

    @Test
    public void equalsNotAClientTest() {
        String testString = "panda";

        assertNotEquals(testString, client);
    }

    @Test
    public void markDeadTest() throws OrganAlreadyRegisteredException {
        client.setOrganDonationStatus(Organ.BONE, true);
        TransplantRequest request = new TransplantRequest(client, Organ.LIVER);
        client.addTransplantRequest(request);

        LocalDate deathDate = LocalDate.now();
        client.markDead(deathDate);

        assertEquals(deathDate, client.getDateOfDeath());
        assertTrue(client.getOrganDonationStatus().get(Organ.BONE));
        assertFalse(client.getOrganRequestStatus().get(Organ.LIVER));
    }

    @Test
    public void profileSearchFullFirst() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("First"));
    }

    @Test
    public void profileSearchHalfFirst() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("Fir"));
    }

    @Test
    public void profileSearchHalfLast() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("La"));
    }

    @Test
    public void profileSearchMiddleTrue() {
        client = new Client("First", "Middle", "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("Mi"));
    }

    @Test
    public void profileSearchMiddleFalse() {
        client = new Client("First", "Middle", "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.profileSearch("ddle"));
    }

    @Test
    public void profileSearchNoMatch() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.profileSearch("Wrong"));
    }

    @Test
    public void profileSearchTwoMatch() {
        client = new Client("First", null, "First", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("First"));
    }

    @Test
    public void profileSearchCaseInsensitive() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("fIrSt"));
    }

    @Test
    public void profileSearchFirstAndLast() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("first last "));
    }

    @Test
    public void profileSearchTwoPrefNames() {
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Jan Michael Vincent");
        assertTrue(client.profileSearch("jan Michael vinc "));
        assertTrue(client.profileSearch("first michael"));
    }


    @Test
    public void testClientIsReceiver() {
        client = new Client("First", null, "First", LocalDate.of(1970, 1, 1), 1);
        TransplantRequest transplantRequest = new TransplantRequest(client, Organ.LIVER);
        client.addTransplantRequest(transplantRequest);
        assertTrue(client.isReceiver());
    }

    @Test
    public void testClientIsDonor() throws OrganAlreadyRegisteredException{
        client = new Client("First", null, "First", LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.HEART, true);
        assertTrue(client.isDonor());
    }
}
