package seng302.Commands;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import picocli.CommandLine;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.BloodType;
import seng302.Utilities.Gender;
import seng302.Utilities.Organ;
import seng302.Utilities.OrganAlreadyRegisteredException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SetOrganStatusTest {

    private DonorManager spyDonorManager;
    private SetOrganStatus spySetOrganStatus;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spySetOrganStatus = spy(new SetOrganStatus(spyDonorManager));

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

    }

    @Test
    public void setorganstatus_invalid_format_id() {
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(0)).run();
    }

    @Test
    public void setorganstatus_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(0)).run();
    }

    @Test
    public void setorganstatus_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spySetOrganStatus, times(1)).run();
        verify(spyDonorManager, times(0)).updateDonor(any());
    }

    @Test
    public void setorganstatus_valid_set_to_true() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--liver"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals(true, captor.getValue().getOrganStatus().get(Organ.LIVER));
    }

    @Test
    public void setorganstatus_valid_set_to_false() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);

        donor.setOrganStatus(Organ.LIVER, true);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--liver=false"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals(false, captor.getValue().getOrganStatus().get(Organ.LIVER));
    }

    @Test
    public void setorganstatus_invalid_set_to_true_already_true() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);

        donor.setOrganStatus(Organ.LIVER, true);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--liver"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        assertThat(outContent.toString(), containsString("Liver is already registered for donation"));
    }

    @Test
    public void setorganstatus_valid_multiple_updates() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--liver", "--kidney"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertEquals(true, captor.getValue().getOrganStatus().get(Organ.LIVER));
        assertEquals(true, captor.getValue().getOrganStatus().get(Organ.KIDNEY));
    }

    @Test
    public void setorganstatus_valid_multiple_updates_some_invalid() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970,1, 1), 1);

        donor.setOrganStatus(Organ.LIVER, true);
        donor.setOrganStatus(Organ.BONE, true);


        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1", "--liver", "--kidney", "--bone=false"};

        ArgumentCaptor<Donor> captor = ArgumentCaptor.forClass(Donor.class);

        CommandLine.run(spySetOrganStatus, System.out, inputs);

        verify(spyDonorManager).updateDonor(captor.capture());

        assertThat(outContent.toString(), containsString("Liver is already registered for donation"));

        assertEquals(true, captor.getValue().getOrganStatus().get(Organ.KIDNEY));
        assertEquals(false, captor.getValue().getOrganStatus().get(Organ.BONE));
    }
}
