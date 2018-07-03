package com.humanharvest.organz.server.controller.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Views;
import com.humanharvest.organz.actions.client.ModifyClientByObjectAction;
import com.humanharvest.organz.ModifyClientObject;
import com.humanharvest.organz.server.exceptions.IfMatchFailedException;
import com.humanharvest.organz.server.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.state.State;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {


    @GetMapping("/clients")
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Client>> getClients() {
        return new ResponseEntity<>(State.getClientManager().getClients(), HttpStatus.OK);
    }

    @PostMapping("/clients")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        State.getClientManager().addClient(client);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("ETag", client.getEtag());

        return new ResponseEntity<>(client, headers, HttpStatus.CREATED);
    }


    @GetMapping("/clients/{id}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Client> getClient(@PathVariable int id) {
        Client client = State.getClientManager().getClientByID(id);
        if (client == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            //Add the new ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("ETag", client.getEtag());

            return new ResponseEntity<>(client, headers, HttpStatus.OK);
        }
    }

    @PatchMapping("/clients/{id}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Client> updateClient(
            @PathVariable int id,
            @RequestBody ModifyClientObject modifyClient,
            @RequestHeader(value = "If-Match", required = false) String ETag)
            throws IfMatchRequiredException, IfMatchFailedException {

        //Logical steps for a PATCH
        //We set If-Match to false so we can return a better error code than 400 which happens if a required
        // @RequestHeader is missing, I think this can be improved with an @ExceptionHandler or similar so we don't
        // duplicate code in tons of places but need to work it out

        //There would be an auth check here. Not yet implemented

        //Fetch the client given by ID
        Client client = State.getClientManager().getClientByID(id);
        if (client == null) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Check the ETag. These are handled in the exceptions class. Really ugly hack to deal with quotes that are
        // messed up for some reason
        if (ETag == null) throw new IfMatchRequiredException();
        if (!client.getEtag().equals(ETag) && !client.getEtag().equals(ETag.substring(1, ETag.length() - 1))) throw new
                IfMatchFailedException();

        //Create the old details to allow undoable action
        ModifyClientObject oldClient = new ModifyClientObject();
        //Copy the values from the current client to our oldClient
        BeanUtils.copyProperties(client, oldClient, modifyClient.getUnmodifiedFields());
        //Make the action (this is a new action)
        ModifyClientByObjectAction action = new ModifyClientByObjectAction(client, State.getClientManager(),
                oldClient, modifyClient);
        //Execute action, this would correspond to a specific users invoker in full version
        State.getInvoker().execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getEtag());

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(client, headers, HttpStatus.OK);
    }

}
