package com.humanharvest.organz.server.controller.client;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.client.ModifyClientOrgansAction;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientDonationStatusController {

    @GetMapping("/clients/{id}/donationStatus")
    public ResponseEntity<Map<Organ, Boolean>> getClientDonationStatus(@PathVariable int id) {
        Optional<Client> client = State.getClientManager().getClientByID(id);
        if (client.isPresent()) {
            //Add the ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("ETag", client.get().getEtag());

            return new ResponseEntity<>(client.get().getOrganDonationStatus(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "One of the organs is already set to the value you gave")
    @ExceptionHandler(OrganAlreadyRegisteredException.class)
    public void ifMatchRequired() {
    }

    @PatchMapping("/clients/{id}/donationStatus")
    public ResponseEntity<Map<Organ, Boolean>> updateClientDonationStatus(
            @PathVariable int id,
            @RequestBody Map<Organ, Boolean> updatedValues,
            @RequestHeader(value = "If-Match", required = false) String ETag)
            throws IfMatchRequiredException, IfMatchFailedException, OrganAlreadyRegisteredException {

        //Logical steps for a PATCH
        //We set If-Match to false so we can return a better error code than 400 which happens if a required
        // @RequestHeader is missing, I think this can be improved with an @ExceptionHandler or similar so we don't
        // duplicate code in tons of places but need to work it out

        //There would be an auth check here. Not yet implemented

        //Fetch the client given by ID
        Optional<Client> client = State.getClientManager().getClientByID(id);
        if (!client.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Check the ETag. These are handled in the exceptions class.
        if (ETag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.get().getEtag().equals(ETag)) {
            throw new
                    IfMatchFailedException();
        }

        //Create the action
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(client.get(), State.getClientManager());
        //Add all the changes. This can throw an OrganAlreadyRegisteredException but we throw it and handle with the
        // above @ExceptionHandler
        for (Entry<Organ, Boolean> updatedValue : updatedValues.entrySet()) {
            action.addChange(updatedValue.getKey(), updatedValue.getValue());
        }

        //Execute action, this would correspond to a specific users invoker in full version
        State.getInvoker().execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("ETag", client.get().getEtag());

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(client.get().getOrganDonationStatus(), headers, HttpStatus.OK);
    }
}
