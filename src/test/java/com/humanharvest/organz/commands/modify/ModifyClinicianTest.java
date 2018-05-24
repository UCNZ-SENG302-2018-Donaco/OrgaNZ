package com.humanharvest.organz.commands.modify;

import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.ClinicianManagerMemory;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

public class ModifyClinicianTest {

    private ClinicianManager spyClinicianManager;
    private ModifyClinician spyModifyClinician;
    private Clinician testClinician;
    private int staffId = 1;
    private int testStaffId = 2;

    @Before
    public void init() {
        spyClinicianManager = spy(new ClinicianManagerMemory());
        spyModifyClinician = Mockito.spy(new ModifyClinician(spyClinicianManager, new ActionInvoker()));

        Clinician clinician = new Clinician("first", "middle", "last",
                "address", Region.CANTERBURY, staffId, "password");
        spyClinicianManager.addClinician(clinician);

        testClinician = new Clinician("first", "middle", "last",
                "address", Region.CANTERBURY, testStaffId, "password");
    }

    @Test
    public void testModifyClinicianInvalidId() {
        String[] inputs = {"-s", "a"};
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(0)).run();
    }

    @Test
    public void testModifyClinicianRegionInvalid() {
        String[] inputs = {"-s", Integer.toString(staffId), "-r", "Not a region"};
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(0)).run();
    }

    @Test
    public void testModifyClinicianRegionValid() {
        String[] inputs = {"-s", Integer.toString(staffId),"-r", "Canterbury"};
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(1)).run();
    }

    @Test
    public void testModifyClinicianNoId() {
        String[] inputs = {"-r", "Canterbury"};
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(0)).run();
    }

    @Test
    public void testModifyClinicianInvalidOption() {
        String[] inputs = {"-s", Integer.toString(staffId),"-r", "Canterbury", "--panda", "panda"};

        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(0)).run();
    }

    @Test
    public void testModifyClinicianFirstName() {
        String newName = "catface";
        String[] inputs = {"-s", Integer.toString(staffId),"-f", newName};
        when(spyClinicianManager.getClinicianByStaffId(anyInt())).thenReturn(testClinician);
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(1)).run();
        assertEquals(newName, testClinician.getFirstName());
    }
}
