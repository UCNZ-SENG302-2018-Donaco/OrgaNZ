package seng302.Commands;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import picocli.CommandLine;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Organ;
import seng302.Utilities.OrganAlreadyRegisteredException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PrintAllOrganTest {

    private DonorManager spyDonorManager;
    private PrintAllOrgan spyPrintAllOrgan;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());

        spyPrintAllOrgan = spy(new PrintAllOrgan(spyDonorManager));
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void printallorgan_no_donors() {
        ArrayList<Donor> donors = new ArrayList<>();

        when(spyDonorManager.getDonors()).thenReturn(donors);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertThat(outContent.toString(), containsString("No donors exist"));
    }

    @Test
    public void printallorgan_single_donor() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);
        donor.setOrganStatus(Organ.LIVER, true);
        donor.setOrganStatus(Organ.KIDNEY, true);

        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);

        when(spyDonorManager.getDonors()).thenReturn(donors);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Kidney, Liver") || outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Liver, Kidney"));
    }

    @Test
    public void printallorgan_multiple_donors() throws OrganAlreadyRegisteredException {
        Donor donor = new Donor("First", "mid", "Last", LocalDate.of(1970,1, 1), 1);
        Donor donor2 = new Donor("FirstTwo", null, "LastTwo", LocalDate.of(1971,2, 2), 2);
        Donor donor3 = new Donor("FirstThree", null, "LastThree", LocalDate.of(1971,2, 2), 3);
        donor.setOrganStatus(Organ.LIVER, true);
        donor.setOrganStatus(Organ.KIDNEY, true);
        donor2.setOrganStatus(Organ.CONNECTIVE_TISSUE, true);

        ArrayList<Donor> donors = new ArrayList<>();
        donors.add(donor);
        donors.add(donor2);
        donors.add(donor3);


        when(spyDonorManager.getDonors()).thenReturn(donors);
        String[] inputs = {};

        CommandLine.run(spyPrintAllOrgan, System.out, inputs);

        assertTrue(outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Kidney, Liver") || outContent.toString().contains("User: 1. Name: First mid Last, Donation status: Liver, Kidney"));
        assertThat(outContent.toString(), containsString("User: 2. Name: FirstTwo LastTwo, Donation status: Connective tissue"));
        assertThat(outContent.toString(), containsString("User: 3. Name: FirstThree LastThree, no organs registered for donation"));
    }
}
