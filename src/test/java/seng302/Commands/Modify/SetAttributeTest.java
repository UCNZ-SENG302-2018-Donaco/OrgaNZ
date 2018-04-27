package seng302.Commands.Modify;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

public class SetAttributeTest {

    private ClientManager spyClientManager;
    private SetAttribute spySetAttribute;

    @Before
    public void initTest() {
        spyClientManager = spy(new ClientManager());

        spySetAttribute = spy(new SetAttribute(spyClientManager, new ActionInvoker()));

    }

    @Test
    public void setAttributeInvalidFormatIdTest() {
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setAttributeInvalidOptionTest() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setattributeNonExistentIdTest() {
        when(spyClientManager.getClientByID(anyInt())).thenReturn(null);
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(1)).run();
    }

    @Test
    public void setAttributeValidNameTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--firstname", "NewFirst"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals("NewFirst", client.getFirstName());
    }

    @Test
    public void setAttributeValidBloodTypeTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--bloodtype", "O+"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(BloodType.O_POS, client.getBloodType());
    }

    @Test
    public void setAttributeInvalidBloodTypeTest() {
        String[] inputs = {"-u", "1", "--bloodtype", "O"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setAttributeValidGenderTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--gender", "Male"};
        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(Gender.MALE, client.getGender());
    }

    @Test
    public void setAttributeInvalidGenderTest() {
        String[] inputs = {"-u", "1", "--gender", "Neg"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setAttributeValidDateTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(client);
        String[] inputs = {"-u", "1", "--dateofdeath", "20/01/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(LocalDate.of(2038, 1, 20), client.getDateOfDeath());
    }


    @Test
    public void setAttributeInvalidDateTest() {
        String[] inputs = {"-u", "1", "--dateofdeath", "20/13/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setAttributeValidRegionTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);

        String[] inputs = {"-u", "1", "--region", "Canterbury"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(Region.CANTERBURY, donor.getRegion());
    }

    @Test
    public void setAttributeValidRegionWithSpaceTest() {
        Donor donor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyDonorManager.getDonorByID(anyInt())).thenReturn(donor);

        String[] inputs = {"-u", "1", "--region", "West Coast"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertEquals(Region.WEST_COAST, donor.getRegion());
    }

    @Test
    public void setAttributeInvalidRegionTest() {
        String[] inputs = {"-u", "1", "--region", "notvalid"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }


}
