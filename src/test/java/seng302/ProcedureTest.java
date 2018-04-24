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
        Person person = new Person();

        LocalDate date = LocalDate.of(2000, 1, 1);
        ProcedureRecord procedure1 = new ProcedureRecord(SUMMARY, DESCRIPTION, date);
        ProcedureRecord procedure2 = new ProcedureRecord(SUMMARY, DESCRIPTION, date, Organ.LIVER);

        person.addProcedure(procedure1);
        person.addProcedure(procedure2);

        assertEquals(2, person.getPastProcedures().count() + person.getPendingProcedures().count());

        person.removeProcedure(procedure1);
        person.removeProcedure(procedure2);

        assertEquals(0, person.getPastProcedures().count() + person.getPendingProcedures().count());
    }
}
