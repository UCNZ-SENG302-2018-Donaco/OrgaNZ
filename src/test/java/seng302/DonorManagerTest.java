package seng302;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DonorManagerTest {

    private DonorManager manager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() {
        manager = new DonorManager();
    }

    @Test
    public void addDonorTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        manager.addDonor(donor);
        assertTrue(manager.getDonors().contains(donor));
    }


    @Test
    public void getDonorsTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        Donor donor2 = new Donor("FirstTwo", null, "LastTwo", LocalDate.of(1970,1, 1), 2);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        donors.add(donor2);
        manager = new DonorManager(donors);

        assertTrue(manager.getDonors().contains(donor));
        assertTrue(manager.getDonors().contains(donor2));
    }

    @Test
    public void removeDonorTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        Donor donor2 = new Donor("FirstTwo", null, "LastTwo", LocalDate.of(1970,1, 1), 2);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        donors.add(donor2);
        manager = new DonorManager(donors);

        manager.removeDonor(donor2);

        assertTrue(manager.getDonors().contains(donor));
        assertFalse(manager.getDonors().contains(donor2));
    }

    @Test
    public void updateDonorTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        Donor donor2 = new Donor("FirstTwo", null, "LastTwo", LocalDate.of(1970,1, 1), 2);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        donors.add(donor2);
        manager = new DonorManager(donors);

        donor.setFirstName("New");

        assertTrue(manager.getDonors().contains(donor));
        assertEquals(manager.getDonorByID(1).getFirstName(), "New");
    }


    @Test
    public void collisionExsistsNoCollisionNameTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        assertFalse(manager.collisionExists("Not", "Same", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void collisionExsistsNoCollisionDateTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        assertFalse(manager.collisionExists("First", "Last", LocalDate.of(2018, 12, 12)));
    }

    @Test
    public void collisionExsistsValidCollisionTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        assertTrue(manager.collisionExists("First", "Last", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void getDonorByIDExistsTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        assertTrue(manager.getDonorByID(1) != null);
    }

    @Test
    public void getDonorByIDDoesNotExistTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        assertTrue(manager.getDonorByID(2) == null);
    }

    @Test
    public void saveToFileTest() throws Exception {
        File file = folder.newFile("testfile.json");

        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        manager.saveToFile(file);

        StringBuilder builder = new StringBuilder();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            builder.append(line);
        }

        String json = builder.toString();

        assertTrue(json.contains("\"firstName\": \"First\""));
        assertTrue(json.contains("\"lastName\": \"Last\""));
    }


    @Test
    public void loadFromFileTest() throws Exception {
        File file = folder.newFile("testfile.json");

        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        manager = new DonorManager(donors);

        manager.saveToFile(file);

        manager = new DonorManager();

        manager.loadFromFile(file);

        assertTrue(manager.getDonors().size() == 1);
        assertEquals("First", manager.getDonors().get(0).getFirstName());
    }

}
