package com.humanharvest.organz.commands.modify;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class SetAttributeTest extends BaseTest {

    private ClientManager spyClientManager;
    private SetAttribute spySetAttribute;

    @BeforeEach
    public void initTest() {
        spyClientManager = spy(new ClientManagerMemory());
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
    public void setAttributeNonExistentIdTest() {
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.empty());
        String[] inputs = {"-u", "2"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(1)).run();
    }

    @Test
    public void setAttributeValidNameTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));
        String[] inputs = {"-u", "1", "--firstname", "NewFirst"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        Assertions.assertEquals("NewFirst", client.getFirstName());
    }

    @Test
    public void setAttributeValidBloodTypeTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));
        String[] inputs = {"-u", "1", "--bloodtype", "O+"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        Assertions.assertEquals(BloodType.O_POS, client.getBloodType());
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
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));
        String[] inputs = {"-u", "1", "--gender", "Male"};
        CommandLine.run(spySetAttribute, System.out, inputs);

        Assertions.assertEquals(Gender.MALE, client.getGender());
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
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));
        String[] inputs = {"-u", "1", "--dateofdeath", "20/01/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        Assertions.assertEquals(LocalDate.of(2038, 1, 20), client.getDateOfDeath());
    }

    @Test
    public void setAttributeInvalidDateTest() {
        String[] inputs = {"-u", "1", "--dateofdeath", "20/13/2038"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        verify(spySetAttribute, times(0)).run();
    }

    @Test
    public void setAttributeValidRegionTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "--region", "Canterbury"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        Assertions.assertEquals(Region.CANTERBURY.toString(), client.getRegion());
    }

    @Test
    public void setAttributeValidRegionWithSpaceTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "--region", "West Coast"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        Assertions.assertEquals(Region.WEST_COAST.toString(), client.getRegion());
    }

    @Test
    public void setAttributeInvalidRegionTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        client.setCountry(Country.NZ);
        client.setRegion(Region.CANTERBURY.toString());
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));

        String[] inputs = {"-u", "1", "--region", "notvalid"};

        CommandLine.run(spySetAttribute, System.out, inputs);

        assertTrue(Region.CANTERBURY.toString().equalsIgnoreCase(client.getRegion()));
    }
}
