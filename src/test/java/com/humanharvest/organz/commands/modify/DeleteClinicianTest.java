package com.humanharvest.organz.commands.modify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.ClinicianManagerMemory;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

public class DeleteClinicianTest extends BaseTest {

    private ClinicianManager spyClinicianManager;
    private DeleteClinician spyDeleteClinician;
    private int staffId = 1;

    @Before
    public void init() {
        spyClinicianManager = spy(new ClinicianManagerMemory());
        spyDeleteClinician = Mockito.spy(new DeleteClinician(spyClinicianManager, new ActionInvoker()));

        Clinician clinician = new Clinician("first", "middle", "last",
                "address", Region.CANTERBURY, staffId, "password");
        spyClinicianManager.addClinician(clinician);
    }

    @Test
    public void ValidDeleteWithYesFlagTest() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", Integer.toString(staffId), "-y"};

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(1)).removeClinician(any());
    }

    @Test
    public void ValidDeleteWithoutYesFlagTest() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", Integer.toString(staffId)};

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(0)).removeClinician(any());
    }

    @Test
    public void testDeleteDefault() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", "0"};

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(0)).removeClinician(any());
    }

    @Test
    public void testDeleteInvalidId() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", "5"};

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(0)).removeClinician(any());
    }

    @Test
    public void testDeleteIdNotInt() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", "q"};

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(0)).removeClinician(any());
    }

    @Test
    public void testDeleteInvalidOption() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", "0", "--invalidOption", "0"};

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(0)).removeClinician(any());
    }
}
