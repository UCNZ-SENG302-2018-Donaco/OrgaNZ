package com.humanharvest.organz.commands.modify;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.ClinicianManagerMemory;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class ModifyClinicianTest extends BaseTest {

    private ClinicianManager spyClinicianManager;
    private ModifyClinician spyModifyClinician;
    private Clinician testClinician;
    private static final int staffId = 1;
    private static final int testStaffId = 2;

    @BeforeEach
    public void init() {
        spyClinicianManager = spy(new ClinicianManagerMemory());
        spyModifyClinician = spy(new ModifyClinician(spyClinicianManager, new ActionInvoker()));

        Clinician clinician = new Clinician("first", "middle", "last",
                "address", Region.CANTERBURY.name(), Country.NZ, staffId, "password");
        spyClinicianManager.addClinician(clinician);

        testClinician = new Clinician("first", "middle", "last",
                "address", Region.CANTERBURY.name(), Country.NZ, testStaffId, "password");
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

        Clinician clinician = spyClinicianManager.getClinicianByStaffId(staffId).orElseThrow(RuntimeException::new);
        assertTrue(Region.CANTERBURY.toString().equalsIgnoreCase(clinician.getRegion()));
    }

    @Test
    public void testModifyClinicianCountryInvalid() {
        String[] inputs = {"-s", Integer.toString(staffId), "-c", "Not a region"};
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(0)).run();
    }

    @Test
    public void testModifyClinicianRegionValid() {
        String[] inputs = {"-s", Integer.toString(staffId), "-r", "Canterbury"};
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
        String[] inputs = {"-s", Integer.toString(staffId), "-r", "Canterbury", "--panda", "panda"};

        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(0)).run();
    }

    @Test
    public void testModifyClinicianFirstName() {
        when(spyClinicianManager.getClinicianByStaffId(anyInt())).thenReturn(
                Optional.ofNullable(testClinician));
        String newName = "catface";
        String[] inputs = {"-s", Integer.toString(staffId), "-f", newName};
        CommandLine.run(spyModifyClinician, System.out, inputs);

        verify(spyModifyClinician, times(1)).run();
        assertEquals(newName, testClinician.getFirstName());
    }
}
