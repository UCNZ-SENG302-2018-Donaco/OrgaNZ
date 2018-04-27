package seng302.Controller.Donor;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import seng302.Controller.ControllerTest;
import seng302.Person;
import seng302.MedicationRecord;
import seng302.Person;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;

public class ViewMedicationsControllerDonorTest extends ControllerTest {

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

    private Person testDonor = new Person();

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICATIONS;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(UserType.PERSON, testDonor);
        mainController.setWindowContext(WindowContext.defaultContext());
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
