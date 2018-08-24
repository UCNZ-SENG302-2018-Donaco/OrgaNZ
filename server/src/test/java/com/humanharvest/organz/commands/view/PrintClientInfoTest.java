package com.humanharvest.organz.commands.view;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PrintClientInfoTest extends BaseTest {

    private ClientManager spyClientManager;
    private PrintClientInfo spyPrintClientInfo;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void init() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        spyClientManager = spy(new ClientManagerMemory());
        spyPrintClientInfo = spy(new PrintClientInfo(spyClientManager));
    }

    @Test
    public void printuserinfo_invalid_format_id() {
        doNothing().when(spyClientManager).addClient(any());
        String[] inputs = {"-u", "notint"};

        CommandLine.run(spyPrintClientInfo, System.out, inputs);

        verify(spyPrintClientInfo, times(0)).run();
    }

    @Test
    public void printuserinfo_invalid_option() {
        String[] inputs = {"-u", "1", "--notanoption"};

        CommandLine.run(spyPrintClientInfo, System.out, inputs);

        verify(spyPrintClientInfo, times(0)).run();
    }

    @Test
    public void printuserinfo_non_existent_id() {
        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.empty());
        String[] inputs = {"-u", "2"};

        CommandLine.run(spyPrintClientInfo, System.out, inputs);

        verify(spyPrintClientInfo, times(1)).run();
        assertThat(outContent.toString(), containsString("No client exists with that user ID"));
    }

    @Test
    public void printuserinfo_valid() {
        Client client = new Client("First", "mid", "Last", LocalDate.of(1970, 1, 1), 1);

        when(spyClientManager.getClientByID(anyInt())).thenReturn(Optional.of(client));
        String[] inputs = {"-u", "1"};

        CommandLine.run(spyPrintClientInfo, System.out, inputs);

        assertThat(outContent.toString(),
                containsString("User: 1. Name: First mid Last, date of birth: 1970-01-01, date of death: null"));
    }
}
