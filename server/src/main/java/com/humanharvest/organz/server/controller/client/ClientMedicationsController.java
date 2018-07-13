package com.humanharvest.organz.server.controller.client;

import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        Client client = State.getClientManager().getClientByID(uid);

        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } else {

            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.getEtag());

            return new ResponseEntity<>(client.getAllMedications(), headers, HttpStatus.OK);
        }
    }


}
