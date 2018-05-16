package seng302.Commands.Modify;

import static org.mockito.Mockito.*;

import seng302.Actions.ActionInvoker;
import seng302.State.ClinicianManager;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class CreateClinicianTest {

    private ClinicianManager spyClientManager;

    private CreateClinician spyCreateClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClinicianManager());
        spyCreateClient = spy(new CreateClinician(spyClientManager, new ActionInvoker()));
    }

    @Test
    public void valid() {
        doNothing().when(spyClientManager).addClinician(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(1)).addClinician(any());
    }


    @Test
    public void invalidFieldCountLow() {
        String[] inputs = {"-f", "Jack"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyCreateClient, times(0)).run();
    }

    @Test
    public void invalidFieldCountHigh() {
        String[] inputs = {"-f", "Jack", "-l", "Steel", "extra"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClinician(any());
    }

    @Test
    public void badRegion() {
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-r", "non-existant"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClinician(any());
    }
}