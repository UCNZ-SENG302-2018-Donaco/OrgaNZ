package seng302.Controller.Client;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.io.IOException;
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
import seng302.Client;
import seng302.MedicationRecord;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;
import seng302.Utilities.Web.MedActiveIngredientsHandler;

import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxRobot;

public class ViewMedicationsControllerClinicianTest extends ControllerTest {

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
            ),
            new MedicationRecord(
                    "A medication that should throw IOException",
                    LocalDate.of(2016, 3, 4),
                    null
            )
    };

    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");
    private Client testClient = new Client();

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICATIONS;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(UserType.CLINICIAN, testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient)
                .build());
        resetTestClientMedicationHistory();
    }

    @Before
    public void resetTestClientMedicationHistory() {
        for (MedicationRecord record : testClient.getPastMedications()) {
            testClient.deleteMedicationRecord(record);
        }
        for (MedicationRecord record : testClient.getCurrentMedications()) {
            testClient.deleteMedicationRecord(record);
        }
        for (MedicationRecord record : testPastMedicationRecords) {
            testClient.addMedicationRecord(record);
        }
        for (MedicationRecord record : testCurrentMedicationRecords) {
            testClient.addMedicationRecord(record);
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
        assertTrue(!testClient.getPastMedications().contains(toBeDeleted));
        assertTrue(!testClient.getCurrentMedications().contains(toBeDeleted));
    }

    // VIEWING ACTIVE INGREDIENTS //

    /**
     * Get the top modal window.
     * @return the top modal window
     */
    private Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        // Return the first found modal window.
        return (Stage) allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks the current alert dialog displayed (on the top of the window stack) has the expected contents.
     * @param expectedHeader Expected header of the dialog
     * @param expectedContent Expected content of the dialog
     */
    private void checkAlertHasHeaderAndContent(String expectedHeader, String expectedContent) {
        final Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertEquals(expectedContent, dialogPane.getContentText());
    }

    /**
     * Create a mock ActiveIngredientsHandler that returns ingredients when passed medName.
     * If medName contains the string "throw IOException", it will throw an IOException.
     * @param medName Name of medication
     * @param ingredients Ingredients in medication
     * @return mock MedActiveIngredientsHandler
     */
    private MedActiveIngredientsHandler createMockActiveIngredientsHandler(String medName, List<String> ingredients)
            throws IOException {
        MedActiveIngredientsHandler handler = mock(MedActiveIngredientsHandler.class);
        if (medName.contains("throw IOException")) {
            when(handler.getActiveIngredients(medName)).thenThrow(new IOException());
        } else {
            try {
                when(handler.getActiveIngredients(medName)).thenReturn(ingredients);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
        return handler;
    }

    @Test
    public void viewActiveIngredientsTest() throws IOException {
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
        checkAlertHasHeaderAndContent("Active ingredients in Ibuprofen", ibuprofenActiveIngredients);
        press(KeyCode.ENTER); // Close the dialog
    }

    @Test
    public void viewActiveIngredientsBadDrugNameTest() throws IOException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
                "Med C",
                Collections.emptyList()
        ));

        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];

        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#viewActiveIngredientsButton");
        checkAlertHasHeaderAndContent("Active ingredients in Med C", "No results found for Med C");
        press(KeyCode.ENTER); // Close the dialog
    }

    @Test
    public void viewActiveIngredientsIOExceptionTest() throws IOException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
                "A medication that should throw IOException",
                Collections.emptyList()
        ));

        MedicationRecord toBeMoved = testCurrentMedicationRecords[2];

        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#viewActiveIngredientsButton");
        checkAlertHasHeaderAndContent(
                "Active ingredients in A medication that should throw IOException",
                "Error loading results. Please try again later.");
        press(KeyCode.ENTER); // Close the dialog
    }
}
