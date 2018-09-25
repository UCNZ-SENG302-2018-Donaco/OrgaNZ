package com.humanharvest.organz.server.controller.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.actions.client.medication.AddMedicationRecordAction;
import com.humanharvest.organz.actions.client.medication.DeleteMedicationRecordAction;
import com.humanharvest.organz.actions.client.medication.ModifyMedicationRecordAction;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientMedicationsController {

    /**
     * The GET endpoint for getting all medications for a given client
     *
     * @param uid the uid of the client
     * @return If successful, a ResponseEntity containing the full list of the clients MedicationRecords is
     * returned
     */
    @GetMapping("/clients/{uid}/medications")
    public ResponseEntity<List<MedicationRecord>> getMedications(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (client.isPresent()) {

            // Check authentication
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());

            HttpHeaders headers = new HttpHeaders();

            return new ResponseEntity<>(client.get().getMedications(), headers, HttpStatus.OK);

        } else {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * The POST endpoint for adding a new medication for a given client
     *
     * @param uid the uid of the client
     * @param medicationRecordView view to create MedicationRecord
     * @return If successful, a ResponseEntity containing all the clients past and current MedicationRecords is
     * returned
     */
    @PostMapping("/clients/{uid}/medications")
    public ResponseEntity<List<MedicationRecord>> postMedication(
            @PathVariable int uid,
            @RequestBody CreateMedicationRecordView medicationRecordView,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Check authentication
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        if (medicationRecordView.getName() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        MedicationRecord record = new MedicationRecord(medicationRecordView.getName(),
                medicationRecordView.getStarted(),
                null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(client.get(),
                record,
                State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        // TODO: Refactor this to fix this issue
        Client client1 = State.getClientManager()
                .getClientByID(client.get().getUid())
                .orElseThrow(IllegalStateException::new);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(client1.getMedications(), headers, HttpStatus.CREATED);
    }

    /**
     * The DELETE endpoint for deleting a MedicationRecord for a client
     *
     * @param uid The uid of the client
     * @param id The id of the medication to delete
     * @return If successful, a ResponseEntity with status CREATED (DELETED) is returned
     */
    @DeleteMapping("/clients/{uid}/medications/{id}")
    public ResponseEntity deleteMedication(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Check authentication
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        MedicationRecord record = client.get().getMedicationRecord(id);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {
            DeleteMedicationRecordAction action = new DeleteMedicationRecordAction(client.get(), record, State
                    .getClientManager());
            State.getActionInvoker(authToken).execute(action);

            Client client1 = State.getClientManager()
                    .getClientByID(client.get().getUid())
                    .orElseThrow(IllegalStateException::new);

            HttpHeaders httpHeaders = new HttpHeaders();

            return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
        }
    }

    /**
     * The POST endpoint for indicating that a medication has been started
     *
     * @param uid The uid of the client
     * @param id The id of the medication to delete
     * @return If successful, a ResponseEntity with the altered record is returned
     */
    @PostMapping("/clients/{uid}/medications/{id}/start")
    public ResponseEntity<MedicationRecord> postMedicationStart(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        // Check authentication
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        MedicationRecord record = client.get().getMedicationRecord(id);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, State.getClientManager());
        action.changeStarted(LocalDate.now());
        action.changeStopped(null);
        State.getActionInvoker(authToken).execute(action);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(record, headers, HttpStatus.OK);
    }

    /**
     * The POST endpoint for indicating that a medication has been stopped
     *
     * @param uid The uid of the client
     * @param id The id of the medication to delete
     * @return If successful, a ResponseEntity with the altered record is returned
     */
    @PostMapping("/clients/{uid}/medications/{id}/stop")
    public ResponseEntity<MedicationRecord> postMedicationStop(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException {

        // Check authentication
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        MedicationRecord record = client.get().getMedicationRecord(id);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, State.getClientManager());
        action.changeStopped(LocalDate.now());
        State.getActionInvoker(authToken).execute(action);

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(record, headers, HttpStatus.OK);
    }
}
