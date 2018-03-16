package seng302.Commands;


import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.DonorManager;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DeleteUserTest {

    private DonorManager spyDonorManager;
    private DeleteUser spyDeleteUser;

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());
        spyDeleteUser = spy(new DeleteUser(spyDonorManager, new ActionInvoker()));

    }

    @Test
    public void deleteuser_invalid_format_id() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyDeleteUser, System.out, inputs);

        verify(spyDeleteUser, times(0)).run();
    }

    @Test
    public void deleteuser_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyDeleteUser, System.out, inputs);

        verify(spyDeleteUser, times(0)).run();
    }

    @Test
    public void deleteuser_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyDeleteUser, System.out, inputs);

        verify(spyDeleteUser, times(1)).run();
        verify(spyDonorManager, times(0)).removeDonor(any());
    }

    @Test
    public void deleteuser_valid_reject() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1"};

        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteUser, System.out, inputs);

        verify(spyDonorManager, times(0)).removeDonor(donor);
    }

    @Test
    public void deleteuser_valid_accept() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1"};

        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        CommandLine.run(spyDeleteUser, System.out, inputs);

        verify(spyDonorManager, times(1)).removeDonor(donor);
    }
}
