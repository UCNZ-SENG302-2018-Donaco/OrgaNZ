package com.humanharvest.organz.commands.modify;

import static org.mockito.Mockito.*;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.ClinicianManagerMemory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import picocli.CommandLine;

public class CreateClinicianTest extends BaseTest {

    private ClinicianManager spyClientManager;

    private CreateClinician spyCreateClient;

    @Before
    public void init() {
        spyClientManager = spy(new ClinicianManagerMemory());
        spyCreateClient = Mockito.spy(new CreateClinician(spyClientManager, new ActionInvoker()));
    }

    @Test
    public void ValidWithStaffIdTest() {
        doNothing().when(spyClientManager).addClinician(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-s", "2"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(1)).addClinician(any());
    }


    @Test
    public void ValidNoStaffIdTest() {
        doNothing().when(spyClientManager).addClinician(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClinician(any());
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

    @Test
    public void duplicateStaffId() {
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-s", "0"};

        CommandLine.run(spyCreateClient, System.out, inputs);

        verify(spyClientManager, times(0)).addClinician(any());
    }
}
