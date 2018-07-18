package com.humanharvest.organz.server.controller.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.actions.client.AddMedicationRecordAction;
import com.humanharvest.organz.actions.client.ModifyMedicationRecordAction;
import com.humanharvest.organz.server.exceptions.IfMatchFailedException;
import com.humanharvest.organz.server.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.state.State;
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
     * @param uid the uid of the client
     * @return Returns a list of all of the past and current medications of the client
     */
    @GetMapping("/clients/{uid}/medications")
    public ResponseEntity<List<MedicationRecord>> getMedications(@PathVariable int uid) {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (client.isPresent()) {

            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.get().getEtag());

            client.get().getAllMedications().add(new MedicationRecord("name", LocalDate.now(), null));

            return new ResponseEntity<>(client.get().getAllMedications(), headers, HttpStatus.OK);

        } else {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/clients/{uid}/medications")
    public ResponseEntity<List<MedicationRecord>> postMedication(
            @PathVariable int uid,
            @RequestBody String medicationName,
            @RequestHeader(value = "If-Match", required = false) String ETag)
            throws IfMatchRequiredException, IfMatchFailedException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else if (ETag == null) {
            throw new IfMatchRequiredException();

        } else if (!client.get().getEtag().equals(ETag)) {
            throw new IfMatchFailedException();
        }

        MedicationRecord record = new MedicationRecord(medicationName, LocalDate.now(), null);
        AddMedicationRecordAction action = new AddMedicationRecordAction(client.get(), record, State.getClientManager());
        State.getInvoker().execute(action);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getEtag());

        return new ResponseEntity<>(client.get().getAllMedications(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/clients/{uid}/medications")
    public ResponseEntity deleteMedication(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String ETag)
            throws IfMatchRequiredException, IfMatchFailedException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else if (ETag == null) {
            throw new IfMatchRequiredException();

        } else if (!client.get().getEtag().equals(ETag)) {
            throw new IfMatchFailedException();
        }

        MedicationRecord record = client.get().getMedicationRecordById(id);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {
            client.get().deleteMedicationRecord(record);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PostMapping("/clients/{uid}/medications/{id}/start")
    public ResponseEntity<MedicationRecord> postMedicationStart(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String ETag)
            throws IfMatchFailedException, IfMatchRequiredException {
        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else if (ETag == null) {
        throw new IfMatchRequiredException();

        } else if (!client.get().getEtag().equals(ETag)) {
            throw new IfMatchFailedException();
        }

        MedicationRecord record = client.get().getMedicationRecordById(id);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, State.getClientManager());
        action.changeStarted(LocalDate.now());
        State.getInvoker().execute(action);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getEtag());

        return new ResponseEntity<>(record, headers, HttpStatus.OK);
    }


    @PostMapping("/clients/{uid}/medications/{id}/stop")
    public ResponseEntity<MedicationRecord> postMedicationStop(
            @PathVariable int uid,
            @PathVariable int id,
            @RequestHeader(value = "If-Match", required = false) String ETag)
            throws IfMatchFailedException, IfMatchRequiredException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);

        if (!client.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else if (ETag == null) {
            throw new IfMatchRequiredException();

        } else if (!client.get().getEtag().equals(ETag)) {
            throw new IfMatchFailedException();
        }

        MedicationRecord record = client.get().getMedicationRecordById(id);

        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ModifyMedicationRecordAction action = new ModifyMedicationRecordAction(record, State.getClientManager());
        action.changeStopped(LocalDate.now());
        State.getInvoker().execute(action);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.get().getEtag());

        return new ResponseEntity<>(record, headers, HttpStatus.OK);
    }
}
