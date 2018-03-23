package seng302.Commands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Donor;
import seng302.DonorManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class PrintAllInfoTest {

    private DonorManager spyDonorManager;
    private PrintAllInfo spyPrintAllInfo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spyPrintAllInfo = spy(new PrintAllInfo(spyDonorManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printallinfo_no_donors() {
        ArrayList<Donor> donors = new ArrayList<>();

        when(spyDonorManager.getDonors()).thenReturn(donors);
        String[] inputs = {};

        CommandLine.run(spyPrintAllInfo, System.out, inputs);

        assertThat(outContent.toString(), containsString("No donors exist"));
    }

    @Test
    public void printallinfo_single_donor() {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);

        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);

        when(spyDonorManager.getDonors()).thenReturn(donors);
        String[] inputs = {};

        CommandLine.run(spyPrintAllInfo, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last, date of birth: 1970-01-01, date of death: null"));
    }

    @Test
    public void printallinfo_multiple_donors() {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);
        Donor donor2 = new Donor("FirstTwo", null, "LastTwo", LocalDate.of(1971,2, 2), 2);

        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        donors.add(donor2);

        when(spyDonorManager.getDonors()).thenReturn(donors);
        String[] inputs = {};

        CommandLine.run(spyPrintAllInfo, System.out, inputs);

        assertThat(outContent.toString(), containsString("User: 1. Name: First mid Last, date of birth: 1970-01-01, date of death: null"));
        assertThat(outContent.toString(), containsString("User: 2. Name: FirstTwo LastTwo, date of birth: 1971-02-02, date of death: null"));
    }
}
