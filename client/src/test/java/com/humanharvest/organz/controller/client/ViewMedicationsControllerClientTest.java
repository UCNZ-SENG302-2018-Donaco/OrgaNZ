package com.humanharvest.organz.controller.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.isVisible;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import javax.imageio.stream.MemoryCacheImageInputStream;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ViewMedicationsControllerClientTest extends ControllerTest {

    MedicationRecord medA = new MedicationRecord(
            "Med A",
            LocalDate.of(2000, 1, 13),
            LocalDate.of(2005, 2, 15)
    );

    MedicationRecord medB = new MedicationRecord(
            "Med B",
            LocalDate.of(2010, 6, 1),
            LocalDate.of(2012, 5, 7)
    );

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

        // Mocking for tests to run without the server

//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.setETag("\"etag\"");

        Client testClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        MedicationRecord record = new MedicationRecord("Name", LocalDate.now(), LocalDate.now());

        //ResponseEntity<MedicationRecord> responseEntityPost = new ResponseEntity<>(record, HttpStatus.CREATED);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag("\"etag\"");

        ResponseEntity getResponse = new ResponseEntity<>(testClient.getAllMedications(), headers, HttpStatus
                .OK);

        //State.getClientManager().addClient(testClient);



        //when(mockRestTemplate.getForEntity(anyString(), new ParameterizedTypeReference<List<MedicationRecord>>() {},
                //any()));

//        when(mockRestTemplate.getForEntity(anyString(), any(), eq(new ParameterizedTypeReference<List<MedicationRecord>>
//                () {}))).thenReturn(getResponse);

//        when(mockRestTemplate.getForEntity(anyString(), any(), eq(List<MedicationRecord>))).thenReturn(getResponse);



        //testClient.addMedicationRecord(record);

        //when(mockRestTemplate.postForEntity(anyString(), any(), eq(MedicationRecord.class))).thenReturn
                //(responseEntityPost);
       // when(mockRestTemplate.getForObject(anyString(), any())).thenReturn(responseEntityPost);

        //when(mockRestTemplate.postForEntity())

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
