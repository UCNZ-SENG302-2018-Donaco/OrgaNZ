package com.humanharvest.organz.server.controller.client;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.client.DeleteClientAction;
import com.humanharvest.organz.actions.client.MarkClientAsDeadAction;
import com.humanharvest.organz.actions.client.ModifyClientByObjectAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.validators.client.ClientBornAndDiedDatesValidator;
import com.humanharvest.organz.utilities.validators.client.CreateClientValidator;
import com.humanharvest.organz.utilities.validators.client.ModifyClientValidator;
import com.humanharvest.organz.views.client.CreateClientView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.SingleDateView;
import com.humanharvest.organz.views.client.Views;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    /**
     * Returns all clients or some optional subset by filtering
     * @return A list of Client overviews
     * @throws AuthenticationException Thrown if the token supplied is invalid, or does not match a clinician or admin
     */
    @GetMapping("/clients")
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Client>> getClients(
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication) throws AuthenticationException {

        //TODO: Add the auth check, but need to remake the login page to not get the list of clients
        //State.getAuthenticationManager().verifyClinicianOrAdmin(authentication);

        List<Client> clients = State.getClientManager().getClients();

        //TODO: Filters
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    /**
     * The POST endpoint for creating a new client
     * @param createClientView The POJO representation of the create client view
     * @return Returns a Client overview. Also contains an ETag header for updates
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @PostMapping("/clients")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Client> createClient(@RequestBody CreateClientView createClientView)
            throws InvalidRequestException {

        //Validate the request, if there are any errors an exception will be thrown.
        if (!CreateClientValidator.isValid(createClientView)) {
            throw new InvalidRequestException();
        }

        //Create a new client with the next available uid
        Client client = new Client(State.getClientManager().nextUid());
        //Copy the details from the CreateClientView to the new Client object
        BeanUtils.copyProperties(createClientView, client);

        //Add the new Client to the manager
        State.getClientManager().addClient(client);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getEtag());

        System.out.println(client);
        return new ResponseEntity<>(client, headers, HttpStatus.CREATED);
    }

    /**
     * The single client GET endpoint
     * @param uid The client UID to return
     * @return Returns a Client details object. Also contains an ETag header for updates
     */
    @GetMapping("/clients/{uid}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Client> getClient(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication) {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            //Authenticate
            State.getAuthenticationManager().verifyClientAccess(authentication, client.get());
            //Add the new ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.get().getEtag());

            return new ResponseEntity<>(client.get(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * The PATCH endpoint for updating a single client
     * @param uid The client UID to update
     * @param modifyClientObject The POJO object of the modifications
     * @param etag The corresponding If-Match header to check for concurrent update handling
     * @return Returns a Client overview. Also contains an ETag header for updates
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException Thrown if the If-Match header does not match the Clients ETag. 412 error
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @PatchMapping("/clients/{uid}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Client> updateClient(
            @PathVariable int uid,
            @RequestBody ModifyClientObject modifyClientObject,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException, AuthenticationException {

        //Logical steps for a PATCH
        //We set If-Match to false so we can return a better error code than 400 which happens if a required
        // @RequestHeader is missing, I think this can be improved with an @ExceptionHandler or similar so we don't
        // duplicate code in tons of places but need to work it out

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        //Check authentication
        State.getAuthenticationManager().verifyClientAccess(authentication, client);

        //Validate the request, if there are any errors an exception will be thrown.
        if (!ModifyClientValidator.isValid(modifyClientObject)) {
            throw new InvalidRequestException();
        }


        //Do some extra validation now that we have the client object. Need to check if a date has been changed, it
        // will not become inconsistent
        if (!ClientBornAndDiedDatesValidator.isValid(modifyClientObject, client)) {
            throw new InvalidRequestException();
        }

        //Check the ETag. These are handled in the exceptions class.
        if (etag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.getEtag().equals(etag)) {
            throw new IfMatchFailedException();
        }


        //Create the old details to allow undoable action
        ModifyClientObject oldClient = new ModifyClientObject();
        //Copy the values from the current client to our oldClient
        BeanUtils.copyProperties(client, oldClient, modifyClientObject.getUnmodifiedFields());
        //Make the action (this is a new action)
        ModifyClientByObjectAction action = new ModifyClientByObjectAction(client,
                State.getClientManager(),
                oldClient,
                modifyClientObject);
        //Execute action, this would correspond to a specific users invoker in full version
        State.getInvoker().execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getEtag());

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(client, headers, HttpStatus.OK);
    }

    /**
     * The DELETE endpoint for removing a single client
     * @param uid The client UID to delete
     * @param etag The corresponding If-Match header to check for concurrent update handling
     * @return Returns an empty body with a simple response code
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException Thrown if the If-Match header does not match the Clients ETag. 412 error
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @DeleteMapping("/clients/{uid}")
    public ResponseEntity deleteClient(
            @PathVariable int uid,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Client client = optionalClient.get();

        State.getAuthenticationManager().verifyClientAccess(authentication, client);

        //Check the ETag. These are handled in the exceptions class.
        if (etag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.getEtag().equals(etag)) {
            throw new IfMatchFailedException();
        }

        DeleteClientAction action = new DeleteClientAction(client, State.getClientManager());
        State.getInvoker().execute(action);

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @PostMapping("/clients/{uid}/dead")
    @JsonView(Views.Details.class)
    public ResponseEntity markClientAsDead(
            @PathVariable int uid,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication,
            @RequestBody SingleDateView dateOfDeath)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Client client = optionalClient.get();

        State.getAuthenticationManager().verifyClientAccess(authentication, client);

        //Check the ETag. These are handled in the exceptions class.
        if (etag == null) {
            throw new IfMatchRequiredException();
        }
        if (!client.getEtag().equals(etag)) {
            throw new IfMatchFailedException();
        }

        MarkClientAsDeadAction action = new MarkClientAsDeadAction(client, dateOfDeath.getDate(), State
                .getClientManager
                ());
        State.getInvoker().execute(action);


        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getEtag());

        //Respond
        return new ResponseEntity<>(client, headers, HttpStatus.OK);
    }

}