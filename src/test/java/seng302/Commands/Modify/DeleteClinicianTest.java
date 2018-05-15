package seng302.Commands.Modify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.io.ByteArrayInputStream;

import seng302.Actions.ActionInvoker;
import seng302.Clinician;
import seng302.State.ClinicianManager;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

public class DeleteClinicianTest {

    private ClinicianManager spyClinicianManager;
    private DeleteClinician spyDeleteClinician;
    private int staffId = 1;

    @Before
    public void init() {
        spyClinicianManager = spy(new ClinicianManager());
        spyDeleteClinician = spy(new DeleteClinician(spyClinicianManager, new ActionInvoker()));

        Clinician clinician = new Clinician("first", "middle", "last",
                "address", Region.CANTERBURY, staffId, "password");
        spyClinicianManager.addClinician(clinician);
    }

    @Test
    public void testValid() {
        doNothing().when(spyClinicianManager).removeClinician(any());
        String[] inputs = {"-s", Integer.toString(staffId)};

        // Input "y" to confirm deleting the clinician
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteClinician, System.out, inputs);

        Mockito.verify(spyClinicianManager, times(1)).removeClinician(any());
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
