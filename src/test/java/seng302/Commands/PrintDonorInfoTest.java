package seng302.Commands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Donor;
import seng302.DonorManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PrintDonorInfoTest {

    private DonorManager spyDonorManager;
    private PrintDonorInfo spyPrintDonorInfo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spyPrintDonorInfo = spy(new PrintDonorInfo(spyDonorManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printuserinfo_invalid_format_id() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyPrintDonorInfo, System.out, inputs);

        verify(spyPrintDonorInfo, times(0)).run();
    }

    @Test
    public void printuserinfo_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyPrintDonorInfo, System.out, inputs);

        verify(spyPrintDonorInfo, times(0)).run();
    }

    @Test
    public void printuserinfo_non_existent_id() {
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyPrintDonorInfo, System.out, inputs);

        verify(spyPrintDonorInfo, times(1)).run();
        assertThat(outContent.toString(), containsString("No donor exists with that user ID"));
    }

    @Test
    public void printuserinfo_valid() {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);

        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);
        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintDonorInfo, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last, date of birth: 1970-01-01, date of death: null"));
    }
}
