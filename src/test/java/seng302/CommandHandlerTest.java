package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.commands.CommandHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Unit test for simple App.
 */

public class CommandHandlerTest {

    private DonorManager spyDonorManager;
    private CommandHandler spyCommandHandler;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());
        spyCommandHandler = spy(new CommandHandler(spyDonorManager));

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @Test
    public void parseCommand_ValidCommand() {
        doNothing().when(spyCommandHandler).createuser(any());
        spyCommandHandler.parseCommand("createuser Jack 21/54/1997");
        verify(spyCommandHandler, times(1)).createuser(any());
    }

    @Test
    public void parseCommand_InvalidCommand() {
        spyCommandHandler.parseCommand("fakecommand");
        assertEquals("Command not found\n", outContent.toString());
    }

    @Test
    public void parseCommand_NoCommand() {
        doNothing().when(spyCommandHandler).createuser(any());
        spyCommandHandler.parseCommand("");
        assertEquals("", outContent.toString());
    }

    @Test
    public void createuser_valid() {
        doNothing().when(spyDonorManager).addDonor(any());
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("TestName");
        inputs.add("01/01/2000");

        spyCommandHandler.createuser(inputs);
        verify(spyDonorManager, times(1)).addDonor(any());
    }

    @Test
    public void createuser_invalidDOB() {
        doNothing().when(spyDonorManager).addDonor(any());
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("TestName");
        inputs.add("0a01/2000");

        spyCommandHandler.createuser(inputs);
        verify(spyDonorManager, times(0)).addDonor(any());
        assertEquals("Invalid input expects form \"createuser {name} {dd/mm/yyyy}\"\n", outContent.toString());
    }


    @Test
    public void createuser_invalidFieldCountHigh() {
        doNothing().when(spyDonorManager).addDonor(any());
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("TestName");
        inputs.add("01/01/2000");
        inputs.add("too many");

        spyCommandHandler.createuser(inputs);
        verify(spyDonorManager, times(0)).addDonor(any());
        assertEquals("Invalid input expects form \"createuser {name} {dd/mm/yyyy}\"\n", outContent.toString());
    }

    @Test
    public void createuser_invalidFieldCountLow() {
        doNothing().when(spyDonorManager).addDonor(any());
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("TestName");

        spyCommandHandler.createuser(inputs);
        verify(spyDonorManager, times(0)).addDonor(any());
        assertEquals("Invalid input expects form \"createuser {name} {dd/mm/yyyy}\"\n", outContent.toString());
    }


    @Test
    public void createuser_duplicateAccept() {
        when(spyDonorManager.collisionExists(any(), any())).thenReturn(true);
        doNothing().when(spyDonorManager).addDonor(any());
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("TestName");
        inputs.add("01/01/2000");

        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);

        spyCommandHandler.createuser(inputs);
        assertEquals("A user already exists with that Name and DOB, would you like to proceed? (y/n)\n", outContent.toString());
        verify(spyDonorManager, times(1)).addDonor(any());
    }

    @Test
    public void createuser_duplicateReject() {
        when(spyDonorManager.collisionExists(any(), any())).thenReturn(true);
        doNothing().when(spyDonorManager).addDonor(any());
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("TestName");
        inputs.add("01/01/2000");

        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);

        spyCommandHandler.createuser(inputs);
        assertEquals("A user already exists with that Name and DOB, would you like to proceed? (y/n)\n", outContent.toString());
        verify(spyDonorManager, times(0)).addDonor(any());
    }

    @Test
    public void printAllDonorInfo_noDonors() {
        spyCommandHandler.printAllDonorInfo();
        assertEquals("No donors exist\n", outContent.toString());
    }

    @Test
    public void printAllDonorInfo_oneDonor() {
        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);

        spyDonorManager.addDonor(donor);

        spyCommandHandler.printAllDonorInfo();
        assertThat(outContent.toString(), containsString("User: 1. Name: TestName, date of birth: 2000-01-01, date of death: null"));
    }

    @Test
    public void printAllDonorInfo_twoDonors() {
        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);
        Donor donor2 = new Donor("TestName2", testDate, 2);

        spyDonorManager.addDonor(donor);
        spyDonorManager.addDonor(donor2);

        spyCommandHandler.printAllDonorInfo();
        assertThat(outContent.toString(), containsString("User: 1. Name: TestName, date of birth: 2000-01-01, date of death: null"));
        assertThat(outContent.toString(), containsString("User: 2. Name: TestName2, date of birth: 2000-01-01, date of death: null"));
    }

    @Test
    public void printUser_valid() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("1");

        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);

        spyDonorManager.addDonor(donor);

        spyCommandHandler.printUser(inputs);
        assertThat(outContent.toString(), containsString("User: 1. Name: TestName, date of birth: 2000-01-01, date of death: null"));
    }

    @Test
    public void printUser_invalidUser() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("2");

        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);

        spyDonorManager.addDonor(donor);

        spyCommandHandler.printUser(inputs);
        assertThat(outContent.toString(), containsString("No donor exists with that user ID"));
    }

    @Test
    public void printUser_invalidFormat() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("q");

        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);

        spyDonorManager.addDonor(donor);

        spyCommandHandler.printUser(inputs);
        assertThat(outContent.toString(), containsString("Invalid user ID, please enter a number"));
    }

    @Test
    public void printUser_invalidFieldCountLow() {
        ArrayList<String> inputs = new ArrayList<>();

        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);

        spyDonorManager.addDonor(donor);

        spyCommandHandler.printUser(inputs);
        System.out.println("Invalid input expects form \"printuser {uid}\"\n");
    }

    @Test
    public void printUser_invalidFieldCountHigh() {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add("1");
        inputs.add("1");

        LocalDate testDate = LocalDate.of(2000,1,1);
        Donor donor = new Donor("TestName", testDate, 1);

        spyDonorManager.addDonor(donor);

        spyCommandHandler.printUser(inputs);
        System.out.println("Invalid input expects form \"printuser {uid}\"\n");
    }



}
