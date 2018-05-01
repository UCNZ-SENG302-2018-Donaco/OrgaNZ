package seng302.Controller.Client;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.IllnessRecord;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;

public class ViewIllnessHistoryClientTest extends ControllerTest{

    private Client testClient = new Client();

    private final IllnessRecord[] testPastIllnessRecords = {
            new IllnessRecord(
                    "Influenza",
                    LocalDate.of(2000, 1, 13),
                    LocalDate.of(2005, 2, 15),
                    false
            ),
            new IllnessRecord(
                    "Clinicial Depression",
                    LocalDate.of(2010, 6, 1),
                    LocalDate.of(2012, 5, 7),
                    false
            )
    };
    private final IllnessRecord[] testCurrentIllnessRecords = {
            new IllnessRecord(
                    "Colon Cancer",
                    LocalDate.of(2014, 3, 4),
                    null,
                    false)
    };

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICAL_HISTORY;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
        resetTestClientIllnessHistory();
    }

    @Before
    public void resetTestClientIllnessHistory() {
        for (IllnessRecord record : testClient.getPastIllnesses()) {
            testClient.deleteIllnessRecord(record);
        }
        for (IllnessRecord record : testClient.getCurrentIllnesses()) {
            testClient.deleteIllnessRecord(record);
        }
        for (IllnessRecord record : testPastIllnessRecords) {
            testClient.addIllnessRecord(record);
        }
        for (IllnessRecord record : testCurrentIllnessRecords) {
            testClient.addIllnessRecord(record);
        }
    }

    @Test
    public void clientCanSeeBothTables(){
        verifyThat("#pastIllnessView",isVisible());
        verifyThat("#currentIllnessView",isVisible());
    }

    @Test
    public void clientCantAddIllness(){
        verifyThat("#illnessNameField",isVisible().negate());

    }

    @Test
    public void clientCantModifyIllnessHistory() {
        verifyThat("#toggleCuredButton", isVisible().negate());
        verifyThat("#toggleChronicButton", isVisible().negate());
        verifyThat("#deleteButton", isVisible().negate());
    }

    @Test
    public void pastIllnessContainsRecordsTest() {
        for (IllnessRecord record : testPastIllnessRecords) {
            verifyThat("#pastIllnessView", containsRow(
                    record.getIllnessName(),
                    record.getDiagnosisDate(),
                    record.getCuredDate()));
        }
    }

    @Test
    public void currentIllnessContainsRecordsTest() {
        for (IllnessRecord record : testCurrentIllnessRecords) {
            verifyThat("#currentIllnessView", containsRow(
                    record.getIllnessName(),
                    record.getDiagnosisDate(),
                    record.isChronic()));
        }
    }
}
