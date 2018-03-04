package seng302.Commands;


import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.CommandHandler;
import seng302.DonorManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateUserTest {

    private DonorManager spyDonorManager;
    private CommandHandler spyCommandHandler;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private CreateUser spyCreateUser;

    @Before
    public void init() {
        spyDonorManager = spy(new DonorManager());
        spyCommandHandler = spy(new CommandHandler(spyDonorManager));

        //System.setOut(new PrintStream(outContent));
        //System.setErr(new PrintStream(errContent));

        spyCreateUser = spy(new CreateUser(spyDonorManager));

    }

    @Test
    public void createuser_valid() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997"};

        CommandLine.run(spyCreateUser, System.out, inputs);

        verify(spyDonorManager, times(1)).addDonor(any());
    }

    @Test
    public void createuser_invalidDOB() {
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/197"};

        CommandLine.run(spyCreateUser, System.out, inputs);

        verify(spyDonorManager, times(0)).addDonor(any());
    }


    @Test
    public void createuser_invalidFieldCountLow() {
        String[] inputs = {"-f", "Jack", "-l", "Steel"};

        CommandLine.run(spyCreateUser, System.out, inputs);

        verify(spyCreateUser, times(0)).run();
    }

    @Test
    public void createuser_invalidFieldCountHigh() {
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997", "extra"};

        CommandLine.run(spyCreateUser, System.out, inputs);

        verify(spyDonorManager, times(0)).addDonor(any());
    }

    @Test
    public void createuser_duplicateAccept() {
        when(spyDonorManager.collisionExists(any(), any(), any())).thenReturn(true);
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997", "--force"};

        CommandLine.run(spyCreateUser, System.out, inputs);

        verify(spyDonorManager, times(1)).addDonor(any());
    }

    @Test
    public void createuser_duplicateReject() {
        when(spyDonorManager.collisionExists(any(), any(), any())).thenReturn(true);
        doNothing().when(spyDonorManager).addDonor(any());
        String[] inputs = {"-f", "Jack", "-l", "Steel", "-d", "21/04/1997"};

        CommandLine.run(spyCreateUser, System.out, inputs);

        verify(spyDonorManager, times(0)).addDonor(any());
    }
}
