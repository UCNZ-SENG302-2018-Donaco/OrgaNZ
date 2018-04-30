//package seng302.Controller.Donor;
//
//import static org.hamcrest.CoreMatchers.not;
//import static org.junit.Assert.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.testfx.api.FxAssert.verifyThat;
//import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
//import static org.testfx.util.NodeQueryUtils.hasText;
//import static org.testfx.util.NodeQueryUtils.isVisible;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.DialogPane;
//import javafx.scene.input.KeyCode;
//import javafx.stage.Stage;
//import javafx.stage.Window;
//
//import seng302.Clinician;
//import seng302.Controller.ControllerTest;
//import seng302.Donor;
//import seng302.MedicationRecord;
//import seng302.State.State;
//import seng302.Utilities.Enums.Region;
//import seng302.Utilities.Exceptions.BadDrugNameException;
//import seng302.Utilities.Exceptions.BadGatewayException;
//import seng302.Utilities.View.Page;
//import seng302.Utilities.View.WindowContext;
//import seng302.Utilities.Web.DrugInteractionsHandler;
//import seng302.Utilities.Web.MedActiveIngredientsHandler;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.testfx.api.FxRobot;
//
//public class ViewMedicationsControllerClinicianTest extends ControllerTest {
//
//    private final MedicationRecord[] testPastMedicationRecords = {
//            new MedicationRecord(
//                    "Med A",
//                    LocalDate.of(2000, 1, 13),
//                    LocalDate.of(2005, 2, 15)
//            ),
//            new MedicationRecord(
//                    "Med B",
//                    LocalDate.of(2010, 6, 1),
//                    LocalDate.of(2012, 5, 7)
//            )
//    };
//    private final MedicationRecord[] testCurrentMedicationRecords = {
//            new MedicationRecord(
//                    "Med C",
//                    LocalDate.of(2014, 3, 4),
//                    null
//            ),
//            new MedicationRecord(
//                    "Ibuprofen",
//                    LocalDate.of(2015, 3, 4),
//                    null
//            ),
//            new MedicationRecord(
//                    "A medication that should throw IOException",
//                    LocalDate.of(2016, 3, 4),
//                    null
//            ),
//            new MedicationRecord(
//                    "Prednisone",
//                    LocalDate.of(2017, 3, 4),
//                    null
//            )
//    };
//
//    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");
//    private Donor testDonor = new Donor(1);
//
//    @Override
//    protected Page getPage() {
//        return Page.VIEW_MEDICATIONS;
//    }
//
//    @Override
//    protected void initState() {
//        State.init();
//        State.login(testClinician);
//        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
//                .setAsClinViewDonorWindow()
//                .viewDonor(testDonor)
//                .build());
//        resetTestDonorMedicationHistory();
//    }
//
//    @Before
//    public void resetTestDonorMedicationHistory() {
//        for (MedicationRecord record : testDonor.getPastMedications()) {
//            testDonor.deleteMedicationRecord(record);
//        }
//        for (MedicationRecord record : testDonor.getCurrentMedications()) {
//            testDonor.deleteMedicationRecord(record);
//        }
//        for (MedicationRecord record : testPastMedicationRecords) {
//            testDonor.addMedicationRecord(record);
//        }
//        for (MedicationRecord record : testCurrentMedicationRecords) {
//            testDonor.addMedicationRecord(record);
//        }
//    }
//
//    @Test
//    public void bothListViewsVisibleTest() {
//        verifyThat("#pastMedicationsView", isVisible());
//        verifyThat("#currentMedicationsView", isVisible());
//    }
//
//    @Test
//    public void newMedicationFieldsVisibleTest() {
//        verifyThat("#newMedField", isVisible());
//    }
//
//    @Test
//    public void modifyButtonsEnabledTest() {
//        verifyThat("#moveToHistoryButton", (Button b) -> !b.isDisabled());
//        verifyThat("#moveToCurrentButton", (Button b) -> !b.isDisabled());
//        verifyThat("#deleteButton", (Button b) -> !b.isDisabled());
//    }
//
//    @Test
//    public void pastMedicationsContainsRecordsTest() {
//        for (MedicationRecord record : testPastMedicationRecords) {
//            verifyThat("#pastMedicationsView", hasListCell(record));
//        }
//    }
//
//    @Test
//    public void currentMedicationsContainsRecordsTest() {
//        for (MedicationRecord record : testCurrentMedicationRecords) {
//            verifyThat("#currentMedicationsView", hasListCell(record));
//        }
//    }
//
//    @Test
//    public void addNewMedicationWithButtonTest() {
//        MedicationRecord toBeAdded = new MedicationRecord("Med D", LocalDate.now(), null);
//
//        clickOn("#newMedField").write(toBeAdded.getMedicationName());
//        clickOn("Add Medication");
//
//        verifyThat("#currentMedicationsView", hasListCell(toBeAdded));
//    }
//
//    @Test
//    public void addNewMedicationWithEnterTest() {
//        MedicationRecord toBeAdded = new MedicationRecord("Med D", LocalDate.now(), null);
//
//        clickOn("#newMedField").write(toBeAdded.getMedicationName());
//        type(KeyCode.ENTER);
//
//        verifyThat("#currentMedicationsView", hasListCell(toBeAdded));
//    }
//
//    @Test
//    public void moveMedicationToPastTest() {
//        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];
//
//        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
//        clickOn("#moveToHistoryButton");
//
//        verifyThat("#pastMedicationsView", hasListCell(toBeMoved));
//        verifyThat("#currentMedicationsView", not(hasListCell(toBeMoved)));
//        assertEquals(toBeMoved.getStopped(), LocalDate.now());
//    }
//
//    @Test
//    public void moveMedicationToCurrentTest() {
//        MedicationRecord toBeMoved = testPastMedicationRecords[0];
//
//        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
//        clickOn("#moveToCurrentButton");
//
//        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
//        verifyThat("#pastMedicationsView", not(hasListCell(toBeMoved)));
//        assertNull(toBeMoved.getStopped());
//    }
//
//    @Test
//    public void deleteMedicationRecordTest() {
//        MedicationRecord toBeDeleted = testPastMedicationRecords[0];
//
//        clickOn((Node) lookup(hasText(toBeDeleted.toString())).query());
//        clickOn("#deleteButton");
//
//        verifyThat("#pastMedicationsView", not(hasListCell(toBeDeleted)));
//        verifyThat("#currentMedicationsView", not(hasListCell(toBeDeleted)));
//        assertTrue(!testDonor.getPastMedications().contains(toBeDeleted));
//        assertTrue(!testDonor.getCurrentMedications().contains(toBeDeleted));
//    }
//
//    // VIEWING ACTIVE INGREDIENTS //
//
//    /**
//     * Get the top modal window.
//     * @return the top modal window
//     */
//    private Stage getTopModalStage() {
//        // Get a list of windows but ordered from top[0] to bottom[n] ones.
//        List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
//        Collections.reverse(allWindows);
//
//        // Return the first found modal window.
//        return (Stage) allWindows
//                .stream()
//                .filter(window -> window instanceof Stage)
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * Checks the current alert dialog displayed (on the top of the window stack) has the expected contents.
//     * @param expectedHeader Expected header of the dialog
//     * @param expectedContent Expected content of the dialog
//     */
//    private void checkAlertHasHeaderAndContent(String expectedHeader, String expectedContent) {
//        final Stage actualAlertDialog = getTopModalStage();
//        assertNotNull(actualAlertDialog);
//
//        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
//        assertEquals(expectedHeader, dialogPane.getHeaderText());
//        assertEquals(expectedContent, dialogPane.getContentText());
//    }
//
//    /**
//     * Create a mock ActiveIngredientsHandler that returns ingredients when passed medName.
//     * If medName contains the string "throw IOException", it will throw an IOException.
//     * @param medName Name of medication
//     * @param ingredients Ingredients in medication
//     * @return mock MedActiveIngredientsHandler
//     */
//    private MedActiveIngredientsHandler createMockActiveIngredientsHandler(String medName, List<String> ingredients)
//            throws IOException {
//        MedActiveIngredientsHandler handler = mock(MedActiveIngredientsHandler.class);
//        if (medName.contains("throw IOException")) {
//            when(handler.getActiveIngredients(medName)).thenThrow(new IOException());
//        } else {
//            try {
//                when(handler.getActiveIngredients(medName)).thenReturn(ingredients);
//            } catch (IOException e) {
//                fail(e.getMessage());
//            }
//        }
//        return handler;
//    }
//
//    @Test
//    public void viewActiveIngredientsTest() throws IOException {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
//                "Ibuprofen",
//                Arrays.asList("Diphenhydramine citrate; ibuprofen",
//                        "Diphenhydramine hydrochloride; ibuprofen",
//                        "Ibuprofen",
//                        "Ibuprofen; pseudoephedrine hydrochloride"
//                )
//        ));
//
//        MedicationRecord ibuprofenRecord = testCurrentMedicationRecords[1];
//        String ibuprofenActiveIngredients = "Diphenhydramine citrate; ibuprofen\n"
//                + "Diphenhydramine hydrochloride; ibuprofen\n"
//                + "Ibuprofen\n"
//                + "Ibuprofen; pseudoephedrine hydrochloride\n";
//
//        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
//        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());
//        clickOn("#viewActiveIngredientsButton");
//        checkAlertHasHeaderAndContent("Active ingredients in Ibuprofen", ibuprofenActiveIngredients);
//        press(KeyCode.ENTER); // Close the dialog
//    }
//
//    @Test
//    public void viewActiveIngredientsBadDrugNameTest() throws IOException {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
//                "Med C",
//                Collections.emptyList()
//        ));
//
//        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];
//
//        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
//        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
//        clickOn("#viewActiveIngredientsButton");
//        checkAlertHasHeaderAndContent("Active ingredients in Med C", "No results found for Med C");
//        press(KeyCode.ENTER); // Close the dialog
//    }
//
//    @Test
//    public void viewActiveIngredientsIOExceptionTest() throws IOException {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setActiveIngredientsHandler(createMockActiveIngredientsHandler(
//                "A medication that should throw IOException",
//                Collections.emptyList()
//        ));
//
//        MedicationRecord toBeMoved = testCurrentMedicationRecords[2];
//
//        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
//        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
//        clickOn("#viewActiveIngredientsButton");
//        checkAlertHasHeaderAndContent(
//                "Active ingredients in A medication that should throw IOException",
//                "Error loading results. Please try again later.");
//        press(KeyCode.ENTER); // Close the dialog
//    }
//
//    //------ Viewing interactions between drugs ------------
//
//    private DrugInteractionsHandler createMockDrugInteractionsHandler(String drug1, String drug2, List<String>
//            interactions) {
//        DrugInteractionsHandler handler = mock(DrugInteractionsHandler.class);
//
//        try {
//            if (drug1.contains("throw IOException") || drug2.contains("throw IOException")) {
//                when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
//                        new IOException("The drug interactions API could not be reached. Check your internet "
//                                + "connection and try again."));
//
//            } else if (drug1.contains("throw IllegalArgumentException") ||
//                    drug2.contains("throw IllegalArgumentException")) {
//                when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
//                        new IllegalArgumentException("The drug interactions API responded in an unexpected way."));
//
//            } else if (drug1.contains("throw BadDrugNameException") || drug2.contains("throw BadDrugNameException")) {
//                when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
//                        new BadDrugNameException("One or both of the drug names are invalid."));
//
//            } else if (drug1.contains("throw BadGatewayException") || drug2.contains("throw BadGatewayException")) {
//                when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
//                        new BadGatewayException("The drug interactions web API could not retrieve the results."));
//
//            } else {
//                when(handler.getInteractions(testDonor, drug1, drug2)).thenReturn(interactions);
//            }
//
//        } catch (IOException | IllegalArgumentException | BadDrugNameException |
//                BadGatewayException exc) {
//            fail(exc.getMessage());
//        }
//
//        return handler;
//    }
//
//    @Test
//    public void viewInteractionsBetweenZeroDrugsTest() {
//        clickOn("#viewInteractionsButton");
//        checkAlertHasHeaderAndContent("Incorrect number of medications selected (0).",
//                "Please select exactly two medications to view their interactions.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsBetweenOneDrugTest() {
//        MedicationRecord drug0 = testCurrentMedicationRecords[0];
//
//        verifyThat("#currentMedicationsView", hasListCell(drug0));
//
//        clickOn((Node) lookup(hasText(drug0.toString())).query());
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Incorrect number of medications selected (1).",
//                "Please select exactly two medications to view their interactions.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsBetweenThreeDrugsTest() {
//        MedicationRecord drug0 = testCurrentMedicationRecords[0];
//        MedicationRecord drug1 = testCurrentMedicationRecords[1];
//        MedicationRecord drug2 = testCurrentMedicationRecords[2];
//
//        verifyThat("#currentMedicationsView", hasListCell(drug0));
//        verifyThat("#currentMedicationsView", hasListCell(drug1));
//        verifyThat("#currentMedicationsView", hasListCell(drug2));
//
//        clickOn((Node) lookup(hasText(drug0.toString())).query());
//        verifyThat((Node) lookup(hasText(drug0.toString())).query(), Node::isFocused);
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(drug1.toString())).query());
//        verifyThat((Node) lookup(hasText(drug1.toString())).query(), Node::isFocused);
//        clickOn((Node) lookup(hasText(drug2.toString())).query());
//        verifyThat((Node) lookup(hasText(drug2.toString())).query(), Node::isFocused);
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Incorrect number of medications selected (3).",
//                "Please select exactly two medications to view their interactions.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsBetweenTwoDrugsTest() {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setDrugInteractionsHandler(
//                createMockDrugInteractionsHandler("Ibuprofen", "Prednisone",
//                        Arrays.asList("anxiety", "arthralgia", "dyspnoea", "fatigue", "nausea", "pyrexia")
//                ));
//
//        MedicationRecord ibuprofenRecord = testCurrentMedicationRecords[1];
//        MedicationRecord prednisoneRecord = testCurrentMedicationRecords[3];
//
//        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
//        verifyThat("#currentMedicationsView", hasListCell(prednisoneRecord));
//
//        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(prednisoneRecord.toString())).query());
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Interactions between Ibuprofen and Prednisone",
//                "anxiety\n"
//                        + "arthralgia\n"
//                        + "dyspnoea\n"
//                        + "fatigue\n"
//                        + "nausea\n"
//                        + "pyrexia");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsBetweenTwoDrugsDifferentListsTest() {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setDrugInteractionsHandler(
//                createMockDrugInteractionsHandler("Med A", "Med C",
//                        Arrays.asList("anxiety", "nausea")
//                ));
//
//        MedicationRecord currentDrug = testCurrentMedicationRecords[0];
//        MedicationRecord pastDrug = testPastMedicationRecords[0];
//
//        verifyThat("#currentMedicationsView", hasListCell(currentDrug));
//        verifyThat("#pastMedicationsView", hasListCell(pastDrug));
//
//        clickOn((Node) lookup(hasText(currentDrug.toString())).query());
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(pastDrug.toString())).query());
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Interactions between Med A and Med C",
//                "anxiety\n"
//                        + "nausea");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsBetweenTwoDrugsNoResultsTest() {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setDrugInteractionsHandler(
//                createMockDrugInteractionsHandler("Ibuprofen", "Prednisone", Collections.emptyList()));
//
//        MedicationRecord ibuprofenRecord = testCurrentMedicationRecords[1];
//        MedicationRecord prednisoneRecord = testCurrentMedicationRecords[3];
//
//        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
//        verifyThat("#currentMedicationsView", hasListCell(prednisoneRecord));
//
//        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(prednisoneRecord.toString())).query());
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Interactions between Ibuprofen and Prednisone",
//                "A study has not yet been done on the interactions between 'Ibuprofen' and 'Prednisone'.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsBadGatewayExceptionTest() {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setDrugInteractionsHandler(
//                createMockDrugInteractionsHandler("throw BadGatewayException", "Med C",
//                        Collections.emptyList()));
//
//        MedicationRecord drug0 = testCurrentMedicationRecords[0];
//        MedicationRecord drug1 = testCurrentMedicationRecords[1];
//
//        verifyThat("#currentMedicationsView", hasListCell(drug0));
//        verifyThat("#currentMedicationsView", hasListCell(drug1));
//
//        clickOn((Node) lookup(hasText(drug0.toString())).query());
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(drug1.toString())).query());
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Interactions between Ibuprofen and Med C",
//                "An error occurred when retrieving drug interactions: \n"
//                        + "The drug interactions web API could not retrieve the results.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsIOExceptionTest() {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setDrugInteractionsHandler(
//                createMockDrugInteractionsHandler("throw IOException", "Med C",
//                        Collections.emptyList()));
//
//        MedicationRecord drug0 = testCurrentMedicationRecords[0];
//        MedicationRecord drug1 = testCurrentMedicationRecords[1];
//
//        verifyThat("#currentMedicationsView", hasListCell(drug0));
//        verifyThat("#currentMedicationsView", hasListCell(drug1));
//
//        clickOn((Node) lookup(hasText(drug0.toString())).query());
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(drug1.toString())).query());
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Interactions between Ibuprofen and Med C",
//                "An error occurred when retrieving drug interactions: \n"
//                        + "The drug interactions API could not be reached. Check your internet connection and try "
//                        + "again.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//
//    @Test
//    public void viewInteractionsIllegalArgumentExceptionTest() {
//        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
//        pageController.setDrugInteractionsHandler(
//                createMockDrugInteractionsHandler("throw IllegalArgumentException", "Med C",
//                        Collections.emptyList()));
//
//        MedicationRecord drug0 = testCurrentMedicationRecords[0];
//        MedicationRecord drug1 = testCurrentMedicationRecords[1];
//
//        verifyThat("#currentMedicationsView", hasListCell(drug0));
//        verifyThat("#currentMedicationsView", hasListCell(drug1));
//
//        clickOn((Node) lookup(hasText(drug0.toString())).query());
//        press(KeyCode.CONTROL); // So all the drugs are selected
//        clickOn((Node) lookup(hasText(drug1.toString())).query());
//        release(KeyCode.CONTROL);
//
//        clickOn("#viewInteractionsButton");
//
//        checkAlertHasHeaderAndContent("Interactions between Ibuprofen and Med C",
//                "An error occurred when retrieving drug interactions: \n"
//                        + "The drug interactions API responded in an unexpected way.");
//        press(KeyCode.ENTER); // Close the dialog
//        release(KeyCode.ENTER);
//    }
//}
