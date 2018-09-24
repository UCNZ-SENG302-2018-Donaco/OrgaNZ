package com.humanharvest.organz.utilities.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Before;
import org.junit.Test;

public class MatchOrganToRecipientsTest {

    private Client recipient1;
    private Client recipient2;
    private Client recipient3;
    private Client donor;
    private LocalDate DATE_OF_BIRTH = LocalDate.now().minusYears(18);
    private Collection<DonatedOrgan> organsToDonate;
    private List<Client> eligibleClients;
    private Organ ORGAN = Organ.LIVER;
    private BloodType BLOOD_TYPE = BloodType.A_POS;
    private Country COUNTRY = Country.NZ;
    private String REGION = Region.CANTERBURY.toString();
    private Set<Hospital> HOSPITALS = Hospital.getDefaultHospitals();
    private Iterator<Hospital> HOSPITAL_ITERATOR = HOSPITALS.iterator();
    private Hospital DONOR_HOSPITAL = HOSPITAL_ITERATOR.next();
    private Hospital SECOND_HOSPITAL = HOSPITAL_ITERATOR.next();

    private Collection<TransplantRequest> allTransplantRequests;

    @Before
    public void setUp() {

        recipient1 = new Client(1);
        recipient2 = new Client(2);
        recipient3 = new Client(3);
        donor = new Client(10);

        // Add clients to list
        List<Client> clients = new ArrayList<>();
        clients.add(recipient1);
        clients.add(recipient2);
        clients.add(recipient3);
        clients.add(donor);

        allTransplantRequests = new ArrayList<>();
        organsToDonate = new ArrayList<>();

        // Make transplant requests for recipients
        TransplantRequest transplantRequest1 = new TransplantRequest(recipient1, ORGAN);
        transplantRequest1.setClient(recipient1);
        allTransplantRequests.add(transplantRequest1);
        TransplantRequest transplantRequest2 = new TransplantRequest(recipient2, ORGAN);
        transplantRequest2.setClient(recipient2);
        allTransplantRequests.add(transplantRequest2);

        for (Client client : clients) {
            client.setCountry(COUNTRY);
            client.setRegion(REGION);
            client.setBloodType(BLOOD_TYPE);
            client.setDateOfBirth(DATE_OF_BIRTH);
        }

        // Setup donor's organ to donate, and death
        LocalDateTime now = LocalDateTime.now();
        donor.donateOrgan(ORGAN, now);
        donor.setDateOfDeath(now.toLocalDate());
        donor.setTimeOfDeath(now.toLocalTime());
        donor.setCountryOfDeath(COUNTRY);
        donor.setRegionOfDeath(REGION);
        donor.setCityOfDeath("Christchurch");
        donor.setHospital(DONOR_HOSPITAL);

        // Check the two hospitals aren't the same
        assertNotEquals(DONOR_HOSPITAL.getName(), SECOND_HOSPITAL.getName());

        organsToDonate.addAll(donor.getDonatedOrgans());
    }

    private void getListOfPotentialRecipients() {
        for (DonatedOrgan donatedOrgan : organsToDonate) {
            eligibleClients = MatchOrganToRecipients.getListOfPotentialRecipients(donatedOrgan, allTransplantRequests);
        }
    }

    @Test
    public void testThirdOrgan() {
        // This test is designed to check that adding a third organ normally works
        TransplantRequest transplantRequest3 = new TransplantRequest(recipient3, ORGAN);
        recipient3.addTransplantRequest(transplantRequest3);
        allTransplantRequests.add(transplantRequest3);

        getListOfPotentialRecipients();
        assertEquals(3, eligibleClients.size());
    }

    // Expired organ

    @Test
    public void testDonatedOrganExpired() {
        donor.setDateOfDeath(LocalDate.now().minusDays(10));

        getListOfPotentialRecipients();
        assertEquals(0, eligibleClients.size());
    }

    // Wrong organ type

    @Test
    public void testWrongOrganType() {
        Organ organ = Organ.BONE_MARROW;
        assertNotEquals(organ, ORGAN); // check this is the wrong organ type
        TransplantRequest transplantRequest3 = new TransplantRequest(recipient3, organ);
        recipient3.addTransplantRequest(transplantRequest3);

        getListOfPotentialRecipients();
        assertEquals(2, eligibleClients.size());
    }

    // Wrong blood type

    @Test
    public void testRecipientNullBloodType() {
        recipient1.setBloodType(null);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
        assertEquals(1, eligibleClients.size());
    }

    @Test
    public void testDonorNullBloodType() {
        donor.setBloodType(null);

        getListOfPotentialRecipients();
        assertEquals(0, eligibleClients.size());
    }

    @Test
    public void testWrongBloodType() {
        BloodType bloodType = BloodType.O_NEG;
        recipient1.setBloodType(bloodType);
        assertNotEquals(bloodType, BLOOD_TYPE);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
        assertEquals(1, eligibleClients.size());
    }

    // Age comparisons

    @Test
    public void testRecipientUnder12() {
        recipient1.setDateOfBirth(LocalDate.now().minusYears(10));

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
        assertEquals(1, eligibleClients.size());
    }

    @Test
    public void testDonorUnder12() {
        donor.setDateOfBirth(LocalDate.now().minusYears(10));

        getListOfPotentialRecipients();
        assertEquals(0, eligibleClients.size());
    }

    @Test
    public void testBothUnder12() {
        recipient1.setDateOfBirth(LocalDate.now().minusYears(10));
        donor.setDateOfBirth(LocalDate.now().minusYears(9));

        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
        assertEquals(1, eligibleClients.size());
    }

    @Test
    public void testOver15YearsApart() {
        recipient1.setDateOfBirth(LocalDate.now().minusYears(50));

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
        assertEquals(1, eligibleClients.size());
    }

    // Request time comparison

    private void testOneRecipientEarlier(Client recipient) throws Exception {
        TransplantRequest transplantRequest3 = new TransplantRequest(recipient1, ORGAN);
        // Use reflection to set the date to a week ago
        Class<?> c = transplantRequest3.getClass();
        Field f = c.getDeclaredField("requestDateTime");
        f.setAccessible(true);
        f.set(transplantRequest3, LocalDateTime.now().minusDays(7));

        recipient.addTransplantRequest(transplantRequest3);
        allTransplantRequests.add(transplantRequest3);

        // this recipient requested earliest
        getListOfPotentialRecipients();
        assertEquals(recipient, eligibleClients.get(0));
    }

    @Test
    public void testOneRecipientEarlier1() throws Exception {
        testOneRecipientEarlier(recipient1);
    }

    @Test
    public void testOneRecipientEarlier2() throws Exception {
        testOneRecipientEarlier(recipient2);
    }

    // Null countries

    @Test
    public void testOneNoCountry() {
        recipient1.setCountry(null);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
        assertEquals(2, eligibleClients.size()); // still allowed if no country
    }

    @Test
    public void testOneNoCountry2() {
        recipient2.setCountry(null);

        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
        assertEquals(2, eligibleClients.size()); // still allowed if no country
    }

    @Test
    public void testBothNoCountry() {
        recipient1.setCountry(null);
        recipient2.setCountry(null);

        getListOfPotentialRecipients();
        assertEquals(2, eligibleClients.size()); // still allowed if no country
    }

    // Different countries

    @Test
    public void testOneInSameCountry() {
        recipient2.setCountry(Country.AU);

        // recipient 1 is in the same country as the donor
        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
    }

    @Test
    public void testOneInSameCountry2() {
        recipient1.setCountry(Country.GB);

        // recipient 2 is in the same country as the donor
        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testBothDifferentCountries() {
        recipient1.setCountry(Country.AU);
        recipient2.setCountry(Country.GB);

        // recipient 1 is much closer to the donor
        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
    }

    @Test
    public void testBothDifferentCountries2() {
        recipient1.setCountry(Country.GB);
        recipient2.setCountry(Country.AU);

        // recipient 2 is much closer to the donor
        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testBothInSameCountryDifferentToDonor() {
        recipient1.setCountry(Country.GB);
        recipient2.setCountry(Country.GB);

        // recipient 2 is in the same country as the donor
        getListOfPotentialRecipients();
        assertEquals(2, eligibleClients.size());
    }

    // Different regions

    @Test
    public void testOneInSameRegion() {
        recipient2.setRegion(Region.BAY_OF_PLENTY.toString());

        // recipient 1 is in the same region as the donor
        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
    }

    @Test
    public void testOneInSameRegion2() {
        recipient1.setRegion(Region.BAY_OF_PLENTY.toString());

        // recipient 2 is in the same region as the donor
        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testBothDifferentRegions() {
        recipient1.setRegion(Region.WEST_COAST.toString());
        recipient2.setRegion(Region.NORTHLAND.toString());

        // recipient 1 is much closer to the donor
        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
    }

    @Test
    public void testBothDifferentRegions2() {
        recipient1.setRegion(Region.NORTHLAND.toString());
        recipient2.setRegion(Region.WEST_COAST.toString());

        // recipient 2 is much closer to the donor
        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testBothInSameRegionDifferentToDonor() {
        recipient1.setRegion(Region.WEST_COAST.toString());
        recipient2.setRegion(Region.WEST_COAST.toString());

        // recipient 2 is in the same country as the donor
        getListOfPotentialRecipients();
        assertEquals(2, eligibleClients.size());
    }

    @Test
    public void testBothDifferentRegionsNonNewZealand() {
        donor.setCountryOfDeath(Country.GB);
        recipient1.setCountry(Country.GB);
        recipient2.setCountry(Country.GB);
        recipient1.setRegion("West Midlands");
        recipient2.setRegion("South East England");

        // we don't know where non-NZ regions are
        getListOfPotentialRecipients();
        assertEquals(2, eligibleClients.size());
    }

    @Test
    public void testOneInUnspecifiedRegion() {
        recipient1.setRegion(Region.SOUTHLAND.toString());
        recipient2.setRegion(Region.UNSPECIFIED.toString());

        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
    }

    @Test
    public void testOneNullRegion() {
        recipient1.setRegion(null);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testBothNullRegions() {
        recipient1.setRegion(null);
        recipient2.setRegion(null);

        getListOfPotentialRecipients();
        assertEquals(2, eligibleClients.size());
    }

    @Test
    public void testHospitalComparison() {
        recipient1.setHospital(SECOND_HOSPITAL);
        recipient2.setHospital(DONOR_HOSPITAL);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testHospitalComparisonNullRegions() {
        recipient1.setRegion(null);
        recipient2.setRegion(null);
        recipient1.setHospital(SECOND_HOSPITAL);
        recipient2.setHospital(DONOR_HOSPITAL);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testOneHospitalNull() {
        recipient1.setHospital(null);
        recipient2.setHospital(DONOR_HOSPITAL);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testOneHospitalNullOneRegionNull() {
        recipient1.setHospital(null);
        recipient2.setRegion(null);
        recipient2.setHospital(DONOR_HOSPITAL);

        getListOfPotentialRecipients();
        assertEquals(recipient2, eligibleClients.get(0));
    }

    @Test
    public void testTwoDifferentHospitals() {
        // Create a fake hospital far away
        Hospital farAwayHospital = new Hospital("middle of the ocean", 0, 0, "Atlantic Ocean");

        recipient1.setHospital(SECOND_HOSPITAL);
        recipient2.setHospital(farAwayHospital);

        getListOfPotentialRecipients();
        assertEquals(recipient1, eligibleClients.get(0));
    }
}
