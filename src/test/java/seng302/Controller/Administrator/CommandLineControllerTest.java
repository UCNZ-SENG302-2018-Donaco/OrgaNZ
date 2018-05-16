package seng302.Controller.Administrator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Test;

public class CommandLineControllerTest extends ControllerTest {

    private static final Client testClient = new Client("Client", "Number", "One", LocalDate.now(), 1);

    @Override
    protected Page getPage() {
        return Page.COMMAND_LINE;
    }

    @Override
    protected void initState() {
        State.init();
        mainController.setWindowContext(WindowContext.defaultContext());
        State.getClientManager().addClient(testClient);

        //Used only for the clipboard test, but needs to be in the initState so it gets executed on the JavaFX thread
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString("line1\nline2\nline3");
        clipboard.setContent(content);
    }

    @Test
    public void checkInitialTextAreaTest() {
        TextArea area = lookup("#outputTextArea").query();
        String outText = area.getText();
        assertTrue(outText.contains("Usage: ClientCLI"));
        assertTrue(outText.contains("Commands:"));
        assertTrue(outText.contains("load"));
    }

    @Test
    public void anInvalidCommandTest() {
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
    public void validCreateUserCommandTest() throws InterruptedException {
        clickOn("#inputTextField").write("createuser -f Jack -l Steel -d 21/04/1997").type(KeyCode.ENTER);

        //Need to give the command a little bit of time to execute
        Thread.sleep(150);

        TextArea area = lookup("#outputTextArea").query();
        assertTrue(area.getText().contains("New client Jack Steel created"));
    }
}
