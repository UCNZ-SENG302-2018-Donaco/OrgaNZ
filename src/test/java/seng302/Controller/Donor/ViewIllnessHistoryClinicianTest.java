package seng302.Controller.Donor;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.IllnessRecord;

import seng302.Person;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;

public class ViewIllnessHistoryClinicianTest extends ControllerTest {

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
    private final  IllnessRecord[] testCurrentIllnessRecords = {
        new IllnessRecord(
            "Colon Cancer",
            LocalDate.of(2014, 3, 4),
            null,
            true
        ),
        new IllnessRecord(
            "Influenza",
            LocalDate.of(2012, 1, 2),
            null,
            false
        ),
        new IllnessRecord(
            "Lung Cancer",
            LocalDate.of(2011, 9, 22),
            null,
            true
        )
    };

    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");
    private Person testDonor = new Person();

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICAL_HISTORY;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(UserType.CLINICIAN, testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewPersonWindow()
                .viewPerson(testDonor)
                .build());
        resetTestDonorIllnessHistory();
    }


    @Before
    public void resetTestDonorIllnessHistory() {
        for (IllnessRecord record : testDonor.getPastIllnesses()) {
            testDonor.deleteIllnessRecord(record);
        }
        for (IllnessRecord record : testDonor.getCurrentIllnesses()) {
            testDonor.deleteIllnessRecord(record);
        }
        for (IllnessRecord record : testPastIllnessRecords) {
            testDonor.addIllnessRecord(record);
        }
        for (IllnessRecord record : testCurrentIllnessRecords) {
            testDonor.addIllnessRecord(record);
        }
    }

    @Test
    public void bothListViewsVisibleTest() {
        verifyThat("#pastIllnessView", isVisible());
        verifyThat("#currentIllnessView", isVisible());
    }

    @Test
    public void newIllnessesFieldsVisibleTest() {
        verifyThat("#IllnessField", isVisible());
    }

    @Test
    public void modifyButtonsEnabledTest() {
        verifyThat("#moveToHistoryButton", (Button b) -> !b.isDisabled());
        verifyThat("#moveToCurrentButton", (Button b) -> !b.isDisabled());
        verifyThat("#deleteButton", (Button b) -> !b.isDisabled());
    }

    /**

    public void removeChronicTag(){
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[0];
        clickOn((Node) lookup(hasText(removeChronicTag.toString())).query());
        clickOn("#noLongerChronic");
        assertEquals(removeChronicTag.toString(),"%s Diagnosed on: %s");


    } **/

    @Test
    public void pastIllnessesContainsRecordsTest() {
        for (IllnessRecord record : testPastIllnessRecords) {
            verifyThat("#pastIllnessView", hasListCell(record));
        }
    }

    @Test
    public void currentIllnessesContainsRecordsTest() {
        for (IllnessRecord record : testCurrentIllnessRecords) {
            verifyThat("#currentIllnessView", hasListCell(record));
        }
    }

    /**
    public void illnessesSortedChronicFirstTest(){
        for (IllnessRecord record : testCurrentIllnessRecords) {
            getList
            verifyThat("#currentIllnessView",g);
            System.out.println(hasListCell(record));
        }

    } **/

    /**

    public void addNewIllnessWithButtonTest() {
        IllnessRecord toBeAdded = new IllnessRecord("Influenza", LocalDate.now(), null,false);

        clickOn("#IllnessField").write(toBeAdded.getIllnessName());
        clickOn("Add Illness");

        verifyThat("#currentIllnessView", hasListCell(toBeAdded));
    } **/

    /**

    public void moveIllnessToPastTest() {
        IllnessRecord toBeMoved = testCurrentIllnessRecords[0];
        System.out.println(toBeMoved.toString());

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToHistoryButton");

        verifyThat("#pastIllnessView", hasListCell(toBeMoved));
        verifyThat("#currentIllnessView", not(hasListCell(toBeMoved)));
        assertEquals(toBeMoved.getCuredDate(), LocalDate.now());
    } **/

    /**


    public void moveIllnesstoCurrentTest() {
        IllnessRecord toBeMoved = testPastIllnessRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToCurrentButton");

        verifyThat("#currentIllnessView", hasListCell(toBeMoved));
        verifyThat("#pastIllnessView", not(hasListCell(toBeMoved)));
        assertNull(toBeMoved.getCuredDate());
    } **/


    @Test
    public void deleteIllnessRecordTest() {
        IllnessRecord toBeDeleted = testPastIllnessRecords[0];

        clickOn((Node) lookup(hasText(toBeDeleted.toString())).query());
        clickOn("#deleteButton");

        verifyThat("#pastIllnessView", not(hasListCell(toBeDeleted)));
        verifyThat("#currentIllnessView", not(hasListCell(toBeDeleted)));
        assertTrue(!testDonor.getPastIllnesses().contains(toBeDeleted));
        assertTrue(!testDonor.getCurrentIllnesses().contains(toBeDeleted));
    }


}
