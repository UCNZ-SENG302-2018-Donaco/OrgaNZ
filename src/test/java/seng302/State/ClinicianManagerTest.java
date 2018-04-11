package seng302.State;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Actions.Clinician.DeleteClinicianAction;
import seng302.Clinician;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ClinicianManagerTest {

    private ClinicianManager manager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() {
        manager = new ClinicianManager();
    }

    @Test
    public void addClinicianTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        manager.addClinician(clinician);
        assertTrue(manager.getClinicians().contains(clinician));
    }


    @Test
    public void getCliniciansTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        Clinician clinician2 = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        clinicians.add(clinician2);
        manager = new ClinicianManager(clinicians);

        assertTrue(manager.getClinicians().contains(clinician));
        assertTrue(manager.getClinicians().contains(clinician2));
    }

    @Test
    public void removeClinicianTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        Clinician clinician2 = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        clinicians.add(clinician2);
        manager = new ClinicianManager(clinicians);

        manager.removeClinician(clinician2);

        assertTrue(manager.getClinicians().contains(clinician));
        assertFalse(manager.getClinicians().contains(clinician2));
    }

    @Test
    public void updateClinicianTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        Clinician clinician2 = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        clinicians.add(clinician2);
        manager = new ClinicianManager(clinicians);

        clinician.setFirstName("New");

        assertTrue(manager.getClinicians().contains(clinician));
        assertEquals(manager.getClinicianByStaffId(1).getFirstName(), "New");
    }


    @Test
    public void collisionExsistsNoCollisionTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        manager = new ClinicianManager(clinicians);

        assertFalse(manager.collisionExists(2));
    }

    @Test
    public void collisionExsistsTrueTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        manager = new ClinicianManager(clinicians);

        assertTrue(manager.collisionExists(1));
    }

    @Test
    public void collisionExsistsTrueMultipleTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        Clinician clinician2 = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        clinicians.add(clinician2);
        manager = new ClinicianManager(clinicians);

        assertTrue(manager.collisionExists(2));
    }
}
