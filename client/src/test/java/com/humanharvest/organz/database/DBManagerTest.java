package com.humanharvest.organz.database;

import java.time.LocalDate;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DBManagerTest extends BaseTest {

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
