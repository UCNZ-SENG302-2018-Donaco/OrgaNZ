package seng302.Database;

import java.time.LocalDate;

import seng302.Client;
import seng302.Clinician;
import seng302.IllnessRecord;
import seng302.MedicationRecord;
import seng302.ProcedureRecord;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;

public class DBManagerTest {

    private DBManager dbManager;

    @Before
    public void setUp() {
        dbManager = DBManager.getInstance();
    }

    @Test
    public void main() throws Exception {
        // Testing that the database works.
        MedicationRecord medRecord = new MedicationRecord("droog", LocalDate.now(), null);

        IllnessRecord illRecord = new IllnessRecord("the cray crays", LocalDate.now().minusYears(1),
                LocalDate.now(), false);

        ProcedureRecord procRecord = new ProcedureRecord("got cut open", "etc", LocalDate.now());
        procRecord.getAffectedOrgans().add(Organ.HEART);
        procRecord.getAffectedOrgans().add(Organ.LIVER);
        procRecord.getAffectedOrgans().add(Organ.SKIN);

        Client client = new Client("Testboi", null, "Testerson", LocalDate.now().minusYears(49), 1);
        client.setDateOfDeath(LocalDate.now());
        client.setGender(Gender.MALE);
        client.setBloodType(BloodType.O_NEG);
        client.setRegion(Region.CANTERBURY);
        client.setWeight(50.0);
        client.setHeight(120.0);
        client.setOrganDonationStatus(Organ.LUNG, true);
        client.setOrganDonationStatus(Organ.INTESTINE, true);
        client.addMedicationRecord(medRecord);
        client.addIllnessRecord(illRecord);
        client.addProcedureRecord(procRecord);
        dbManager.saveEntity(client);

        Clinician clinician = new Clinician("Testwoman", "Malorie", "Testerson", "The Moon", Region.NELSON,
                1001, "therecanbeonlyone");
        dbManager.saveEntity(clinician);
    }
}
