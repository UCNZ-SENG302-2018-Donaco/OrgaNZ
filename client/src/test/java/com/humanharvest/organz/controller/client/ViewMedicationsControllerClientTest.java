package com.humanharvest.organz.controller.client;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ViewMedicationsControllerClientTest extends ControllerTest {

    private static final MedicationRecord[] testPastMedicationRecords = {
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
    private static final MedicationRecord[] testCurrentMedicationRecords = {
            new MedicationRecord(
                    "Med C",
                    LocalDate.of(2014, 3, 4),
                    null
            )
    };

    private Client testClient = new Client(1);

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICATIONS;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
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
    public void newMedicationFieldsNotVisibleTest() {
        verifyThat("#newMedField", isVisible().negate());
    }

    @Test
    public void modifyButtonsDisabledTest() {
        verifyThat("#moveToHistoryButton", isDisabled());
        verifyThat("#moveToCurrentButton", isDisabled());
        verifyThat("#deleteButton", isDisabled());
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
}
