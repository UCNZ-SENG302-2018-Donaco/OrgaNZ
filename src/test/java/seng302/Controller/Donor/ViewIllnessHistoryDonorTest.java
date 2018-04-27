package seng302.Controller.Donor;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import javafx.scene.Node;

import seng302.Controller.ControllerTest;
import seng302.IllnessRecord;
import seng302.Person;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;

public class ViewIllnessHistoryDonorTest extends ControllerTest{

    private Person testClient = new Person();

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
        State.login(UserType.PERSON,testClient);
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
        verifyThat("#IllnessField",isVisible().negate());

    }

    @Test
    public void clientCantModifyIllnessHistory() {
        verifyThat("#moveToHistoryButton", isDisabled());
        verifyThat("#moveToCurrentButton", isDisabled());
        verifyThat("#deleteButton", isDisabled());
    }


    @Test
    public void pastIllnessContainsRecordsTest() {
        for (IllnessRecord record : testPastIllnessRecords) {
            verifyThat("#pastIllnessView", hasListCell(record));
        }
    }

    @Test
    public void currentIllnessContainsRecordsTest() {
        for (IllnessRecord record : testCurrentIllnessRecords) {
            verifyThat("#currentIllnessView", hasListCell(record));
        }
    }









}
