package seng302;

import org.junit.Test;

import java.time.LocalDate;

import seng302.Utilities.Enums.Organ;

import static org.junit.Assert.*;

public class ProcedureTest {
    private static final String SUMMARY = "Summary1";
    private static final String DESCRIPTION = "Description1";

    @Test
    public void testAddRemove() {
        Donor donor = new Donor(1);

        LocalDate date = LocalDate.of(2000, 1, 1);
        ProcedureRecord procedure1 = new ProcedureRecord(SUMMARY, DESCRIPTION, date);
        ProcedureRecord procedure2 = new ProcedureRecord(SUMMARY, DESCRIPTION, date, Organ.LIVER);

        donor.addProcedure(procedure1);
        donor.addProcedure(procedure2);

        assertEquals(2, donor.getPastProcedures().count() + donor.getPendingProcedures().count());

        donor.removeProcedure(procedure1);
        donor.removeProcedure(procedure2);

        assertEquals(0, donor.getPastProcedures().count() + donor.getPendingProcedures().count());
    }
}
