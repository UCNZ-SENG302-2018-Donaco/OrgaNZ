package seng302.Commands.Modify;


import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.State.DonorManager;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class DeleteDonorTest {

    private DonorManager spyDonorManager;
    private DeleteDonor spyDeleteDonor;

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());
        spyDeleteDonor = spy(new DeleteDonor(spyDonorManager, new ActionInvoker()));

    }

    @Test
    public void deleteuser_invalid_format_id() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyDeleteDonor, System.out, inputs);

        verify(spyDeleteDonor, times(0)).run();
    }

    @Test
    public void deleteuser_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyDeleteDonor, System.out, inputs);

        verify(spyDeleteDonor, times(0)).run();
    }

    @Test
    public void deleteuser_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyDeleteDonor, System.out, inputs);

        verify(spyDeleteDonor, times(1)).run();
        verify(spyDonorManager, times(0)).removeDonor(any());
    }

    @Test
    public void deleteuser_valid_reject() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1"};

        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteDonor, System.out, inputs);

        verify(spyDonorManager, times(0)).removeDonor(donor);
    }

    @Test
    public void deleteuser_valid_accept() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1"};

        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteDonor, System.out, inputs);

        verify(spyDonorManager, times(1)).removeDonor(donor);
    }
}
