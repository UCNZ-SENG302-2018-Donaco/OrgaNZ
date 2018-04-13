package seng302.Utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import seng302.Donor;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;

import org.junit.Test;

public class JSONFileReaderTest {

    private void checkDeserializedDonorsFromValidFile(List<Donor> outputDonors) {
        // Check that all 95 donors were deserialized.
        assertEquals(95, outputDonors.size());

        // Tests on the 50th donor in the file to check that all visible fields were deserialized correctly.
        Donor testDonor = outputDonors.get(50);
        assertEquals("Jaeden", testDonor.getFirstName());
        assertEquals("Cade", testDonor.getMiddleName());
        assertEquals("Nelson", testDonor.getLastName());
        assertEquals(LocalDate.of(1966, 7, 1), testDonor.getDateOfBirth());
        assertNull(testDonor.getDateOfDeath());
        assertEquals(Gender.MALE, testDonor.getGender());
        assertEquals(210.0, testDonor.getHeight(), 0.0001);
        assertEquals(143.0, testDonor.getWeight(), 0.0001);
        assertEquals(BloodType.B_NEG, testDonor.getBloodType());
        assertEquals("145 Old Lane", testDonor.getCurrentAddress());
        assertEquals(Region.NELSON, testDonor.getRegion());
        assertEquals(LocalDateTime.of(2018, 3, 21, 20, 55, 46, 275000000), testDonor.getCreationdate());

        // Test that the donor's organs set was also deserialized correctly.
        List<Organ> expectedOrgans = Arrays.asList(
                Organ.HEART, Organ.LUNG, Organ.SKIN, Organ.PANCREAS, Organ.KIDNEY,Organ.BONE
        );
        Map<Organ, Boolean> testOrgans = testDonor.getOrganStatus();
        for (Organ organ : testOrgans.keySet()) {
            if (expectedOrgans.contains(organ)) {
                assertTrue(testOrgans.get(organ));
            } else {
                assertFalse(testOrgans.get(organ));
            }
        }
    }

    @Test
    public void readAllFromValidFileTest() throws Exception {
        File inputFile = new File("src/test/resources/many_donors.json");
        JSONFileReader<Donor> donorFileReader = new JSONFileReader<>(inputFile, Donor.class);
        List<Donor> outputDonors = donorFileReader.getAll();
        donorFileReader.close();

        checkDeserializedDonorsFromValidFile(outputDonors);
    }

    @Test
    public void readAsStreamFromValidFileTest() throws Exception {
        File inputFile = new File("src/test/resources/many_donors.json");
        JSONFileReader<Donor> donorFileReader = new JSONFileReader<>(inputFile, Donor.class);
        List<Donor> outputDonors = new ArrayList<>();
        Donor current;

        assertEquals(0, donorFileReader.getFilePosition());

        donorFileReader.startStream();
        while ((current = donorFileReader.getNext()) != null) {
            /*System.out.println(
                    String.format("%d/%d bytes read", donorFileReader.getFilePosition(), donorFileReader.getFileSize())
            );*/
            outputDonors.add(current);
        }

        assertEquals(donorFileReader.getFileSize(), donorFileReader.getFilePosition());
        donorFileReader.close();

        checkDeserializedDonorsFromValidFile(outputDonors);
    }

    @Test(expected = IllegalStateException.class)
    public void readAsStreamWithoutStartingStreamTest() throws Exception {
        File inputFile = new File("src/test/resources/many_donors.json");
        JSONFileReader<Donor> donorFileReader = new JSONFileReader<>(inputFile, Donor.class);

        donorFileReader.getNext();
    }

    @Test(expected = IllegalStateException.class)
    public void readAllAfterStartingStreamTest() throws Exception {
        File inputFile = new File("src/test/resources/many_donors.json");
        JSONFileReader<Donor> donorFileReader = new JSONFileReader<>(inputFile, Donor.class);

        donorFileReader.startStream();
        donorFileReader.getAll();
    }
}
