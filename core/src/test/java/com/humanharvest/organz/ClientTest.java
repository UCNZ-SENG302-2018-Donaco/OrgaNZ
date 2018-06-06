package com.humanharvest.organz;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Period;

import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {
    private static final String FIRST_NAME = "First";
    private static final String MIDDLE_NAME = "middlename";
    private static final String PREFERRED_NAME = "Preferred";

    private Client client;

    @BeforeEach
    void createClient() {
        client = new Client(1);
    }

    @Test
    void getBMI1Test() {
        client.setWeight(70);
        client.setHeight(180);
        assertEquals(21.6, client.getBMI(), 0.01);
    }

    @Test
    void getBMI2Test() {
        client.setWeight(0);
        client.setHeight(180);
        assertEquals(0, client.getBMI(), Double.MIN_NORMAL);
    }

    @Test
    void getAgeTest() {
        LocalDate dob = LocalDate.of(2000, 1, 1);
        int age = Period.between(dob, LocalDate.now()).getYears();
        client.setDateOfBirth(dob);
        assertEquals(client.getAge(), age);
    }

    @Test
    void getAge2Test() {
        LocalDate dob = LocalDate.of(2000, 1, 1);
        LocalDate dod = LocalDate.of(2010, 1, 1);
        client.setDateOfBirth(dob);
        client.setDateOfDeath(dod);
        assertEquals(10, client.getAge());
    }

    @Test
    void CheckNameContainsValidTest() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains(FIRST_NAME));
    }

    @Test
    void CheckNameContainsCaseInsensitivityValidTest() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("first"));
    }

    @Test
    void CheckNameContainsLastNameValidTest() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("La"));
    }

    @Test
    void CheckNameContainsMiddleNameValidTest() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("mid"));
    }

    @Test
    void CheckNameContainsPreferredNameValidTest() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName(PREFERRED_NAME);
        assertTrue(client.nameContains(PREFERRED_NAME));
    }

    @Test
    void CheckNameContainsNotValidTest() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.nameContains("notin"));
    }

    @Test
    void CheckNameContainsMultipleChecksValidTest() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.nameContains("F Last"));
    }

    @Test
    void CheckNameContainsMultipleChecksOneInvalidTest() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.nameContains("F mid not"));
    }

    @Test
    void GetFullNameNoMiddleNameNoPreferredNameTest() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertEquals("First Last", client.getFullName());
    }

    @Test
    void GetFullNameWithMiddleNameNoPreferredNameTest() {
        client = new Client(FIRST_NAME, "Mid Name", "Last", LocalDate.of(1970, 1, 1), 1);
        assertEquals("First Mid Name Last", client.getFullName());
    }

    @Test
    void GetFullNameNoMiddleNameWithPreferredNameTest() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Pref");
        assertEquals("First \"Pref\" Last", client.getFullName());
    }

    @Test
    void GetFullNameWithMiddleNameWithPreferredNameTest() {
        client = new Client(FIRST_NAME, "Mid Name", "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Pref");
        assertEquals("First Mid Name \"Pref\" Last", client.getFullName());
    }

    @Test
    void noRequestCurrentOrganRequest1Test() {
        assertFalse(client.isReceiver());
    }


    @Test
    void validCurrentOrganRequestTest() {
        Organ o = Organ.HEART;
        TransplantRequest t = new TransplantRequest(client, o);
        client.addTransplantRequest(t);
        assertTrue(client.isReceiver());
    }

    @Test
    void getOrganStatusStringInvalidStringTest() {
        assertThrows(IllegalArgumentException.class, () -> client.getOrganStatusString(""));
    }

    @Test
    void getOrganStatusStringValid2Test() throws OrganAlreadyRegisteredException {
        Organ o = Organ.HEART;
        client.setOrganDonationStatus(o, true);
        assertEquals("Heart", client.getOrganStatusString("donations"));
    }

    @Test
    void testEqualsNotAClientTest() {
        String testString = "panda";

        assertNotEquals(testString, client);
    }

    @Test
    void markDeadTest() throws OrganAlreadyRegisteredException {
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
    void profileSearchFullFirst() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch(FIRST_NAME));
    }

    @Test
    void profileSearchHalfFirst() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("Fir"));
    }

    @Test
    void profileSearchHalfLast() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("La"));
    }

    @Test
    void profileSearchMiddleTrue() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("Mi"));
    }

    @Test
    void profileSearchMiddleFalse() {
        client = new Client(FIRST_NAME, MIDDLE_NAME, "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.profileSearch("ddle"));
    }

    @Test
    void profileSearchNoMatch() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertFalse(client.profileSearch("Wrong"));
    }

    @Test
    void profileSearchTwoMatch() {
        client = new Client(FIRST_NAME, null, FIRST_NAME, LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch(FIRST_NAME));
    }

    @Test
    void profileSearchCaseInsensitive() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("fIrSt"));
    }

    @Test
    void profileSearchFirstAndLast() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        assertTrue(client.profileSearch("first last "));
    }

    @Test
    void profileSearchTwoPrefNames() {
        client = new Client(FIRST_NAME, null, "Last", LocalDate.of(1970, 1, 1), 1);
        client.setPreferredName("Jan Michael Vincent");
        assertTrue(client.profileSearch("jan Michael vinc "));
        assertTrue(client.profileSearch("first michael"));
    }


    @Test
    void testClientIsReceiver() {
        client = new Client(FIRST_NAME, null, FIRST_NAME, LocalDate.of(1970, 1, 1), 1);
        TransplantRequest transplantRequest = new TransplantRequest(client, Organ.LIVER);
        client.addTransplantRequest(transplantRequest);
        assertTrue(client.isReceiver());
    }

    @Test
    void testClientIsDonor() throws OrganAlreadyRegisteredException{
        client = new Client(FIRST_NAME, null, FIRST_NAME, LocalDate.of(1970, 1, 1), 1);
        client.setOrganDonationStatus(Organ.HEART, true);
        assertTrue(client.isDonor());
    }
}
