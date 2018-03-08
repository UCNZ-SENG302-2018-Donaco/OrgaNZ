package seng302.Commands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Organ;
import seng302.Utilities.OrganAlreadyRegisteredException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PrintUserOrganTest {

    private DonorManager spyDonorManager;
    private PrintUserOrgan spyPrintUserOrgan;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spyPrintUserOrgan = spy(new PrintUserOrgan(spyDonorManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printuserorgan_invalid_format_id() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyPrintUserOrgan, System.out, inputs);

        verify(spyPrintUserOrgan, times(0)).run();
    }

    @Test
    public void printuserorgan_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyPrintUserOrgan, System.out, inputs);

        verify(spyPrintUserOrgan, times(0)).run();
    }

    @Test
    public void printuserorgan_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyPrintUserOrgan, System.out, inputs);

        verify(spyPrintUserOrgan, times(1)).run();
        assertThat(outContent.toString(), containsString("No donor exists with that user ID"));
    }

    @Test
    public void printuserorgan_valid_no_organs() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);

        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintUserOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last, no organs registered for donation"));
    }

    @Test
    public void printuserorgan_valid_one_organ() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);
        donor.setOrganStatus(Organ.KIDNEY, true);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);

        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintUserOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last, Donation status: Kidney"));
    }
    @Test
    public void printuserorgan_valid_multiple_organs() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);
        donor.setOrganStatus(Organ.LIVER, true);
        donor.setOrganStatus(Organ.KIDNEY, true);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);

        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintUserOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Kidney, Liver") || outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Liver, Kidney"));
    }
}
