package com.humanharvest.organz.controller.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import com.humanharvest.organz.utilities.web.DrugInteractionsHandler;
import com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler;

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
            ),
            new MedicationRecord(
                    "Prednisone",
                    LocalDate.of(2017, 3, 4),
                    null
            )
    };

    private Client testClient;

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICATIONS;
    }

    @Override
    protected void initState() {
        State.reset();

        Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED.toString(), null, 0, "E");
        testClient = new Client(1);

        State.login(testClinician);
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());

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
        verifyThat("#moveToHistoryButton", (Button button) -> !button.isDisabled());
        verifyThat("#moveToCurrentButton", (Button button) -> !button.isDisabled());
        verifyThat("#deleteButton", (Button button) -> !button.isDisabled());
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
        clickOn("#newMedField").write("Med D");
        clickOn("Add Medication");

        //Assert that the currentMedications list contains an entry with name "Med D"
        assertThat(testClient.getCurrentMedications()).extracting("medicationName").contains("Med D");
    }

    @Test
    public void addNewMedicationWithEnterTest() {
        clickOn("#newMedField").write("Med D");
        press(KeyCode.ENTER);

        //Assert that the currentMedications list contains an entry with name "Med D"
        assertThat(testClient.getCurrentMedications()).extracting("medicationName").contains("Med D");
    }

    @Test
    public void moveMedicationToPastTest() {
        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToHistoryButton");

        verifyThat("#pastMedicationsView", hasListCell(toBeMoved));
        verifyThat("#currentMedicationsView", not(hasListCell(toBeMoved)));
        assertEquals(LocalDate.now(), toBeMoved.getStopped());
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
     *
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
     *
     * @param expectedHeader  Expected header of the dialog
     * @param expectedContent Expected content of the dialog
     */
    private void checkAlertHasHeaderAndContent(String expectedHeader, String expectedContent) {
        Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertEquals(expectedContent, dialogPane.getContentText());
    }

    /**
     * Create a mock ActiveIngredientsHandler that returns ingredients when passed medName.
     * If medName contains the string "throw IOException", it will throw an IOException.
     *
     * @param medName     Name of medication
     * @param ingredients Ingredients in medication
     * @return mock MedActiveIngredientsHandler
     */
    private MedActiveIngredientsHandler createMockActiveIngredientsHandler(String medName, List<String> ingredients)
            throws IOException {
        MedActiveIngredientsHandler handler = mock(MedActiveIngredientsHandler.class);
        if (medName.contains("throw IOException")) {
            when(handler.getActiveIngredients(medName)).thenThrow(new IOException());
        } else {
            when(handler.getActiveIngredients(medName)).thenReturn(ingredients);
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
                + "Ibuprofen; pseudoephedrine hydrochloride";

        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());

        TextArea medicationIngredients = lookup("#medicationIngredients").query();
        String ingredientsInfo = medicationIngredients.getText();
        assertEquals("Active ingredients in Ibuprofen: \n" + ibuprofenActiveIngredients, ingredientsInfo);
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

        String medicationInfo = ((TextInputControl) lookup("#medicationIngredients").query()).getText();
        assertEquals("No active ingredients found for Med C", medicationInfo);
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

        String medicationInfo = ((TextInputControl) lookup("#medicationIngredients").query()).getText();
        assertEquals("Error loading ingredients, please try again later", medicationInfo);
    }

    //------ Viewing interactions between drugs ------------

    private DrugInteractionsHandler createMockDrugInteractionsHandler(String drug1, String drug2, List<String>
            interactions) throws BadGatewayException, IOException, BadDrugNameException {
        DrugInteractionsHandler handler = mock(DrugInteractionsHandler.class);

        if (drug1.contains("throw IOException") || drug2.contains("throw IOException")) {
            when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
                    new IOException("The drug interactions API could not be reached. Check your internet "
                            + "connection and try again."));

        } else if (drug1.contains("throw IllegalArgumentException") ||
                drug2.contains("throw IllegalArgumentException")) {
            when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
                    new IllegalArgumentException("The drug interactions API responded in an unexpected way."));

        } else if (drug1.contains("throw BadDrugNameException") || drug2.contains("throw BadDrugNameException")) {
            when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
                    new BadDrugNameException("One or both of the drug names are invalid."));

        } else if (drug1.contains("throw BadGatewayException") || drug2.contains("throw BadGatewayException")) {
            when(handler.getInteractions(any(), anyString(), anyString())).thenThrow(
                    new BadGatewayException("The drug interactions web API could not retrieve the results."));

        } else {
            when(handler.getInteractions(testClient, drug1, drug2)).thenReturn(interactions);
        }

        return handler;
    }

    @Test
    public void viewInteractionsBetweenOneDrugTest() {
        MedicationRecord drug0 = testCurrentMedicationRecords[0];

        verifyThat("#currentMedicationsView", hasListCell(drug0));

        clickOn((Node) lookup(hasText(drug0.toString())).query());

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("", interactionsInfo);
    }

    @Test
    public void viewInteractionsBetweenThreeDrugsTest() {
        MedicationRecord drug0 = testCurrentMedicationRecords[0];
        MedicationRecord drug1 = testCurrentMedicationRecords[1];
        MedicationRecord drug2 = testCurrentMedicationRecords[2];

        verifyThat("#currentMedicationsView", hasListCell(drug0));
        verifyThat("#currentMedicationsView", hasListCell(drug1));
        verifyThat("#currentMedicationsView", hasListCell(drug2));

        clickOn((Node) lookup(hasText(drug0.toString())).query());
        clickOn((Node) lookup(hasText(drug1.toString())).query());
        clickOn((Node) lookup(hasText(drug2.toString())).query());

        // Make sure that the third node selected does not select
        Node node = lookup(hasText(drug2.toString())).query();
        assertFalse(node.isFocused());
    }

    @Test
    public void viewInteractionsBetweenTwoDrugsTest() throws BadGatewayException, IOException, BadDrugNameException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setDrugInteractionsHandler(
                createMockDrugInteractionsHandler("Ibuprofen", "Prednisone",
                        Arrays.asList("anxiety", "arthralgia", "dyspnoea", "fatigue", "nausea", "pyrexia")
                ));

        MedicationRecord ibuprofenRecord = testCurrentMedicationRecords[1];
        MedicationRecord prednisoneRecord = testCurrentMedicationRecords[3];

        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
        verifyThat("#currentMedicationsView", hasListCell(prednisoneRecord));

        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());
        press(KeyCode.CONTROL); // So all the drugs are selected
        clickOn((Node) lookup(hasText(prednisoneRecord.toString())).query());
        release(KeyCode.CONTROL);

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("Interactions between Ibuprofen and Prednisone: \n"
                + "anxiety\n"
                + "arthralgia\n"
                + "dyspnoea\n"
                + "fatigue\n"
                + "nausea\n"
                + "pyrexia", interactionsInfo);
    }

    @Test
    public void viewInteractionsBetweenTwoDrugsDifferentListsTest() throws BadGatewayException, IOException, BadDrugNameException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setDrugInteractionsHandler(
                createMockDrugInteractionsHandler("Med A", "Med C",
                        Arrays.asList("anxiety", "nausea")
                ));

        MedicationRecord currentDrug = testCurrentMedicationRecords[0];
        MedicationRecord pastDrug = testPastMedicationRecords[0];

        verifyThat("#currentMedicationsView", hasListCell(currentDrug));
        verifyThat("#pastMedicationsView", hasListCell(pastDrug));

        clickOn((Node) lookup(hasText(currentDrug.toString())).query());
        press(KeyCode.CONTROL); // So all the drugs are selected
        clickOn((Node) lookup(hasText(pastDrug.toString())).query());
        release(KeyCode.CONTROL);

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("Interactions between Med A and Med C: \n"
                + "anxiety\n"
                + "nausea", interactionsInfo);
    }

    @Test
    public void viewInteractionsBetweenTwoDrugsNoResultsTest() throws BadGatewayException, IOException, BadDrugNameException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setDrugInteractionsHandler(
                createMockDrugInteractionsHandler("Ibuprofen", "Prednisone", Collections.emptyList()));

        MedicationRecord ibuprofenRecord = testCurrentMedicationRecords[1];
        MedicationRecord prednisoneRecord = testCurrentMedicationRecords[3];

        verifyThat("#currentMedicationsView", hasListCell(ibuprofenRecord));
        verifyThat("#currentMedicationsView", hasListCell(prednisoneRecord));

        clickOn((Node) lookup(hasText(ibuprofenRecord.toString())).query());
        press(KeyCode.CONTROL); // So all the drugs are selected
        clickOn((Node) lookup(hasText(prednisoneRecord.toString())).query());
        release(KeyCode.CONTROL);

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("There is no information on interactions between Ibuprofen and Prednisone.", interactionsInfo);
    }

    @Test
    public void viewInteractionsBadGatewayExceptionTest() throws BadGatewayException, IOException, BadDrugNameException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setDrugInteractionsHandler(
                createMockDrugInteractionsHandler("throw BadGatewayException", "Med C",
                        Collections.emptyList()));

        MedicationRecord drug0 = testCurrentMedicationRecords[0];
        MedicationRecord drug1 = testCurrentMedicationRecords[1];

        verifyThat("#currentMedicationsView", hasListCell(drug0));
        verifyThat("#currentMedicationsView", hasListCell(drug1));

        clickOn((Node) lookup(hasText(drug0.toString())).query());
        press(KeyCode.CONTROL); // So all the drugs are selected
        clickOn((Node) lookup(hasText(drug1.toString())).query());
        release(KeyCode.CONTROL);

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("An error occurred when retrieving drug interactions: \n"
                + "The drug interactions web API could not retrieve the results.", interactionsInfo);
    }

    @Test
    public void viewInteractionsIOExceptionTest() throws BadGatewayException, IOException, BadDrugNameException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setDrugInteractionsHandler(
                createMockDrugInteractionsHandler("throw IOException", "Med C",
                        Collections.emptyList()));

        MedicationRecord drug0 = testCurrentMedicationRecords[0];
        MedicationRecord drug1 = testCurrentMedicationRecords[1];

        verifyThat("#currentMedicationsView", hasListCell(drug0));
        verifyThat("#currentMedicationsView", hasListCell(drug1));

        clickOn((Node) lookup(hasText(drug0.toString())).query());
        press(KeyCode.CONTROL); // So all the drugs are selected
        clickOn((Node) lookup(hasText(drug1.toString())).query());
        release(KeyCode.CONTROL);

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("An error occurred when retrieving drug interactions: \n"
                + "The drug interactions API could not be reached. Check your internet connection and try "
                + "again.", interactionsInfo);
    }

    @Test
    public void viewInteractionsIllegalArgumentExceptionTest() throws BadGatewayException, IOException, BadDrugNameException {
        ViewMedicationsController pageController = (ViewMedicationsController) super.pageController;
        pageController.setDrugInteractionsHandler(
                createMockDrugInteractionsHandler("throw IllegalArgumentException", "Med C",
                        Collections.emptyList()));

        MedicationRecord drug0 = testCurrentMedicationRecords[0];
        MedicationRecord drug1 = testCurrentMedicationRecords[1];

        verifyThat("#currentMedicationsView", hasListCell(drug0));
        verifyThat("#currentMedicationsView", hasListCell(drug1));

        clickOn((Node) lookup(hasText(drug0.toString())).query());
        press(KeyCode.CONTROL); // So all the drugs are selected
        clickOn((Node) lookup(hasText(drug1.toString())).query());
        release(KeyCode.CONTROL);

        String interactionsInfo = ((TextInputControl) lookup("#medicationInteractions").query()).getText();
        assertEquals("An error occurred when retrieving drug interactions: \n"
                + "The drug interactions API responded in an unexpected way.", interactionsInfo);
    }

}
