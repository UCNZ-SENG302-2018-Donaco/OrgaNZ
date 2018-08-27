package com.humanharvest.organz.controller.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.resolvers.CommandRunner;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class CommandLineControllerTest extends ControllerTest {

    private static final Client testClient = new Client("Client", "Number", "One", LocalDate.now(), 1);

    private CommandRunner oldValue;
    private CommandRunner commandRunnerMock;

    @Override
    protected Page getPage() {
        return Page.COMMAND_LINE;
    }

    @Override
    protected void initState() {
        State.reset();

        commandRunnerMock = mock(CommandRunner.class);
        try {
            oldValue = setPrivateField(State.class, "commandRunner", commandRunnerMock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        when(commandRunnerMock.execute("help")).thenReturn("Usage: OrgaNZ\nCommands:\nload");

        State.login(new Administrator("username", "password"));
        mainController.setWindowContext(WindowContext.defaultContext());

        State.getClientManager().addClient(testClient);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        setPrivateField(State.class, "commandRunner", oldValue);
    }

    @Test
    public void checkInitialTextAreaTest() {
        TextArea area = lookup("#outputTextArea").query();
        String outText = area.getText();
        assertTrue(outText.contains("Usage: OrgaNZ"));
        assertTrue(outText.contains("Commands:"));
        assertTrue(outText.contains("load"));
    }

    @Test
    public void anInvalidCommandTest() {
        when(commandRunnerMock.execute("notacmd")).thenReturn("Unmatched argument [notacmd]");

        clickOn("#inputTextField").write("notacmd").type(KeyCode.ENTER);

        TextArea area = lookup("#outputTextArea").query();
        assertTrue(area.getText().contains("Unmatched argument [notacmd]"));
    }

    @Test
    public void upDownArrowsTest() {
        clickOn("#inputTextField").write("notacmd").type(KeyCode.ENTER);
        clickOn("#inputTextField").write("second").type(KeyCode.ENTER);
        clickOn("#inputTextField").write("third").type(KeyCode.ENTER);
        clickOn("#inputTextField").write("fourth").type(KeyCode.ENTER);
        clickOn("#inputTextField").write("newtext").type(KeyCode.UP);

        verifyThat("#inputTextField", hasText("fourth"));

        type(KeyCode.UP);

        verifyThat("#inputTextField", hasText("third"));

        type(KeyCode.UP);
        type(KeyCode.UP);

        verifyThat("#inputTextField", hasText("notacmd"));

        type(KeyCode.UP);

        verifyThat("#inputTextField", hasText("notacmd"));

        type(KeyCode.DOWN);

        verifyThat("#inputTextField", hasText("second"));

        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);

        verifyThat("#inputTextField", hasText("newtext"));

        type(KeyCode.DOWN);
        type(KeyCode.DOWN);

        verifyThat("#inputTextField", hasText("newtext"));
    }

    @Test
    public void pasteMultipleLinesTest() {
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString("line1\nline2\nline3");
            clipboard.setContent(content);
        });

        when(commandRunnerMock.execute("line1")).thenReturn("Unmatched argument [line1]");
        when(commandRunnerMock.execute("line2")).thenReturn("Unmatched argument [line2]");

        clickOn("#inputTextField");

        //Paste text, text specified in the initState section "line1\nline2\nline3"
        press(KeyCode.CONTROL);
        type(KeyCode.V);
        release(KeyCode.CONTROL);

        TextArea area = lookup("#outputTextArea").query();
        String outText = area.getText();
        assertTrue(outText.contains("Unmatched argument [line1]"));
        assertTrue(outText.contains("Unmatched argument [line2]"));
        assertFalse(outText.contains("Unmatched argument [line3]"));
        verifyThat("#inputTextField", hasText("line3"));
    }

    @Test
    public void pasteSingleLineTest() {
        clickOn("#inputTextField").write("copy text");

        clickOn("#inputTextField");
        clickOn("#inputTextField");
        clickOn("#inputTextField");

        press(KeyCode.CONTROL);
        type(KeyCode.C);
        release(KeyCode.CONTROL);
        type(KeyCode.DELETE);
        write("new text ");

        press(KeyCode.CONTROL);
        type(KeyCode.V);
        release(KeyCode.CONTROL);

        verifyThat("#inputTextField", hasText("new text copy text"));
    }

    @Test
    public void validCreateClientCommandTest() {
        when(commandRunnerMock.execute("createclient -f Jack -l Steel -d 21/04/1997")).thenReturn(
                "Created client Jack Steel with user id: 2");

        clickOn("#inputTextField").write("createclient -f Jack -l Steel -d 21/04/1997").type(KeyCode.ENTER);

        //Need to give the command a little bit of time to execute and force a refresh (TestFX bug)
        write("waiting");
        type(KeyCode.ENTER);

        TextArea area = lookup("#outputTextArea").query();
        assertTrue(area.getText().contains("Created client Jack Steel with user id:"));
    }
}
