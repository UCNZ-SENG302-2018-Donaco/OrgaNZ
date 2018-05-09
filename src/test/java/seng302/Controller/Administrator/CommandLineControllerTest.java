package seng302.Controller.Administrator;

import static junit.framework.TestCase.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.time.LocalDate;

import javafx.scene.control.TextArea;

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

    @Test@Ignore
    public void aCommandTest() {
        TextArea area = lookup("#outputTextArea").query();
        assertTrue(area.getText().contains("Usage: ClientCLI"));
        assertTrue(area.getText().contains("Commands:"));
        assertTrue(area.getText().contains("load"));
    }
}
