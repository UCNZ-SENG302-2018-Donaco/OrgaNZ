package seng302.Controller.Administrator;

import static junit.framework.TestCase.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Ignore;
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
    }

    @Test
    public void checkInitialTextAreaTest() {
        TextArea area = lookup("#outputTextArea").query();
        assertTrue(area.getText().contains("Usage: ClientCLI"));
        assertTrue(area.getText().contains("Commands:"));
        assertTrue(area.getText().contains("load"));
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
}
