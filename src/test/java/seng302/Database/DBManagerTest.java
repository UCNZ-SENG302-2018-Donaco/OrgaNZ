package seng302.Database;

import java.time.LocalDate;

import seng302.IllnessRecord;
import seng302.MedicationRecord;
import seng302.ProcedureRecord;
import seng302.Utilities.Enums.Organ;

import org.junit.Before;
import org.junit.Test;

public class DBManagerTest {
    private DBManager dbManager;

    @Before
    public void setUp() {
        dbManager = new DBManager();
    }

    @Test
    public void main() {
        // Testing that the database works.
        MedicationRecord medRecord = new MedicationRecord("droog", LocalDate.now(), null);
        dbManager.saveEntity(medRecord);

        IllnessRecord illRecord = new IllnessRecord("the cray crays", LocalDate.now().minusYears(1),
                LocalDate.now(), false);
        dbManager.saveEntity(illRecord);

        ProcedureRecord procRecord = new ProcedureRecord("got cut open", "etc", LocalDate.now());
        procRecord.getAffectedOrgans().add(Organ.HEART);
        procRecord.getAffectedOrgans().add(Organ.LIVER);
        procRecord.getAffectedOrgans().add(Organ.SKIN);

        dbManager.saveEntity(procRecord);
    }
}
