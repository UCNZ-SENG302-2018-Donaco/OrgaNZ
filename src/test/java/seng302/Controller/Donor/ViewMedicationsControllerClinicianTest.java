package seng302.Controller.Donor;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.Donor;
import seng302.MedicationRecord;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;
import seng302.Utilities.Web.MedActiveIngredientsHandler;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.testfx.api.FxRobot;

public class ViewMedicationsControllerClinicianTest extends ControllerTest {

    // how long to wait (in ms) between checks (e.g. that data has loaded from the internet)
    private final static int CYCLE = 500;

    private final MedicationRecord[] testPastMedicationRecords = {
            new MedicationRecord(
                    "Med A",
                    LocalDate.of(2000, 1, 13),
                    LocalDate.of(2005, 2, 15)
            ),
            new MedicationRecord(
                    "Med B",
                    LocalDate.of(2010, 6, 1),
                    LocalDate.of(2012, 5, 7)
            )
    };
    private final MedicationRecord[] testCurrentMedicationRecords = {
            new MedicationRecord(
                    "Med C",
                    LocalDate.of(2014, 3, 4),
                    null
            ),
            new MedicationRecord(
                    "Ibuprofen",
                    LocalDate.of(2015, 3, 4),
                    null
            )
    };

    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");
    private Donor testDonor = new Donor();


    /**
     * Checks the current alert dialog displayed (on the top of the window stack) has the expected contents.
     *
     * From https://stackoverflow.com/a/48654878/8355496
     * Licenced under cc by-sa 3.0 with attribution required https://creativecommons.org/licenses/by-sa/3.0/
     * @param expectedHeader Expected header of the dialog
     * @param expectedContent Expected content of the dialog
     */
    private void alertDialogHasHeaderAndContent(final String expectedHeader, final String expectedContent) {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertEquals(expectedContent, dialogPane.getContentText());
    }

    /**
     * Get the top modal window.
     *
     * Adapted from https://stackoverflow.com/a/48654878/8355496
     * Licenced under cc by-sa 3.0 with attribution required https://creativecommons.org/licenses/by-sa/3.0/
     * @return the top modal window
     */
    private Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        // It is needed to get the first found modal window.
        final List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        return (Stage) allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks the current alert dialog displayed (on the top of the window stack) has the expected contents.
     * It will wait for the contents to change from "Loading..." before failing it.
     * @param expectedHeader Expected header of the dialog
     * @param expectedContent Expected content of the dialog
     */
    private void alertDialogHasHeaderAndContentAfterLoading(final String expectedHeader, final String expectedContent) {

        // Check that the dialog box is what is expected (after it had loaded the data from the API server)
        boolean loading = true;
        while (loading) {
            sleep(CYCLE); //wait 1 cycle
            try {
                alertDialogHasHeaderAndContent(expectedHeader, expectedContent);
                loading = false;
            } catch (ComparisonFailure e) {
                try {
                    alertDialogHasHeaderAndContent(expectedHeader, "Loading...");
                } catch (ComparisonFailure e2) {
                    // If it isn't loading, then return a comparison failure that compares it to what it should
                    // actually be, not to "Loading...".
                    alertDialogHasHeaderAndContent(expectedHeader, expectedContent);
                }
            }
        }
    }

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICATIONS;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(UserType.CLINICIAN, testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewDonorWindow()
                .viewDonor(testDonor)
                .build());
        resetTestDonorMedicationHistory();
    }

    @Before
    public void resetTestDonorMedicationHistory() {
        for (MedicationRecord record : testDonor.getPastMedications()) {
            testDonor.deleteMedicationRecord(record);
        }
        for (MedicationRecord record : testDonor.getCurrentMedications()) {
            testDonor.deleteMedicationRecord(record);
        }
        for (MedicationRecord record : testPastMedicationRecords) {
            testDonor.addMedicationRecord(record);
        }
        for (MedicationRecord record : testCurrentMedicationRecords) {
            testDonor.addMedicationRecord(record);
        }
    }

    @Test
    public void bothListViewsVisibleTest() {
        verifyThat("#pastMedicationsView", isVisible());
        verifyThat("#currentMedicationsView", isVisible());
    }

    @Test
    public void newMedicationFieldsVisibleTest() {
        verifyThat("#newMedField", isVisible());
    }

    @Test
    public void modifyButtonsEnabledTest() {
        verifyThat("#moveToHistoryButton", (Button b) -> !b.isDisabled());
        verifyThat("#moveToCurrentButton", (Button b) -> !b.isDisabled());
        verifyThat("#deleteButton", (Button b) -> !b.isDisabled());
    }

    @Test
    public void pastMedicationsContainsRecordsTest() {
        for (MedicationRecord record : testPastMedicationRecords) {
            verifyThat("#pastMedicationsView", hasListCell(record));
        }
    }

    @Test
    public void currentMedicationsContainsRecordsTest() {
        for (MedicationRecord record : testCurrentMedicationRecords) {
            verifyThat("#currentMedicationsView", hasListCell(record));
        }
    }

    @Test
    public void addNewMedicationWithButtonTest() {
        MedicationRecord toBeAdded = new MedicationRecord("Med D", LocalDate.now(), null);

        clickOn("#newMedField").write(toBeAdded.getMedicationName());
        clickOn("Add Medication");

        verifyThat("#currentMedicationsView", hasListCell(toBeAdded));
    }

    @Test
    public void addNewMedicationWithEnterTest() {
        MedicationRecord toBeAdded = new MedicationRecord("Med D", LocalDate.now(), null);

        clickOn("#newMedField").write(toBeAdded.getMedicationName());
        type(KeyCode.ENTER);

        verifyThat("#currentMedicationsView", hasListCell(toBeAdded));
    }

    @Test
    public void moveMedicationToPastTest() {
        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToHistoryButton");

        verifyThat("#pastMedicationsView", hasListCell(toBeMoved));
        verifyThat("#currentMedicationsView", not(hasListCell(toBeMoved)));
        assertEquals(toBeMoved.getStopped(), LocalDate.now());
    }

    @Test
    public void moveMedicationToCurrentTest() {
        MedicationRecord toBeMoved = testPastMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToCurrentButton");

        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
        verifyThat("#pastMedicationsView", not(hasListCell(toBeMoved)));
        assertNull(toBeMoved.getStopped());
    }

    @Test
    public void deleteMedicationRecordTest() {
        MedicationRecord toBeDeleted = testPastMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeDeleted.toString())).query());
        clickOn("#deleteButton");

        verifyThat("#pastMedicationsView", not(hasListCell(toBeDeleted)));
        verifyThat("#currentMedicationsView", not(hasListCell(toBeDeleted)));
        assertTrue(!testDonor.getPastMedications().contains(toBeDeleted));
        assertTrue(!testDonor.getCurrentMedications().contains(toBeDeleted));
    }

    //------ Viewing active ingredients ------------

    private MedActiveIngredientsHandler createMockActiveIngredientsHandler(String medName, List<String> ingredients) {
        MedActiveIngredientsHandler handler = mock(MedActiveIngredientsHandler.class);
        when(handler.getActiveIngredients(medName)).thenReturn(ingredients);
        return handler;
    }

    @Test
    public void viewActiveIngredientsTest() {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
                "Ibuprofen",
                Arrays.asList("Diphenhydramine citrate; ibuprofen",
                        "Diphenhydramine hydrochloride; ibuprofen",
                        "Ibuprofen",
                        "Ibuprofen; pseudoephedrine hydrochloride"
                )
        ));

        MedicationRecord ibuprofenRecord = testCurrentMedicationRecords[1];
        String ibuprofenActiveIngredients = "Diphenhydramine citrate; ibuprofen\n"
                + "Diphenhydramine hydrochloride; ibuprofen\n"
                + "Ibuprofen\n"
                + "Ibuprofen; pseudoephedrine hydrochloride\n";

        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());
        clickOn("#viewActiveIngredientsButton");
        alertDialogHasHeaderAndContentAfterLoading("Active ingredients in Ibuprofen", ibuprofenActiveIngredients);
        press(KeyCode.ENTER); // Close the dialog, ready for the next test.
    }

    @Test
    public void viewActiveIngredientsBadDrugNameTest() {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
                "Med C",
                Collections.emptyList()
        ));

        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];

        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#viewActiveIngredientsButton");
        alertDialogHasHeaderAndContentAfterLoading("Active ingredients in Med C", "No results found for Med C");
        press(KeyCode.ENTER); // Close the dialog, ready for the next test.
    }

}
