package seng302.Database;

import java.time.LocalDate;

import seng302.MedicationRecord;

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
    }
}
