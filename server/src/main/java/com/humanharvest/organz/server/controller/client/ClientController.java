package com.humanharvest.organz.server.controller.client;

import static com.humanharvest.organz.utilities.validators.ClientValidator.checkClientETag;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.CreateClientAction;
import com.humanharvest.organz.actions.client.DeleteClientAction;
import com.humanharvest.organz.actions.client.MarkClientAsDeadAction;
import com.humanharvest.organz.actions.client.ModifyClientByObjectAction;
import com.humanharvest.organz.actions.images.AddImageAction;
import com.humanharvest.organz.actions.images.DeleteImageAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.validators.client.ClientBornAndDiedDatesValidator;
import com.humanharvest.organz.utilities.validators.client.CreateClientValidator;
import com.humanharvest.organz.utilities.validators.client.ModifyClientValidator;
import com.humanharvest.organz.views.client.CreateClientView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    private static final Logger LOGGER = Logger.getLogger(ClientController.class.getName());

    /**
     * Returns all clients or some optional subset by filtering
     *
     * @return A list of Client overviews
     * @throws AuthenticationException Thrown if the token supplied is invalid, or does not match a clinician or admin
     */
    @GetMapping("/clients")
    @JsonView(Views.Overview.class)
    public ResponseEntity<PaginatedClientList> getClients(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Integer minimumAge,
            @RequestParam(required = false) Integer maximumAge,
            @RequestParam(required = false) Set<String> regions,
            @RequestParam(required = false) EnumSet<Gender> birthGenders,
            @RequestParam(required = false) ClientType clientType,
            @RequestParam(required = false) EnumSet<Organ> donating,
            @RequestParam(required = false) EnumSet<Organ> requesting,
            @RequestParam(required = false) ClientSortOptionsEnum sortOption,
            @RequestParam(required = false) Boolean isReversed
    ) throws AuthenticationException {

        //TODO: Add the auth check, but need to remake the login page to not get the list of clients
        //State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        //We need to remove the space characters here. Possibly needs to be done for other types as well
        Set<String> newRegions = new HashSet<>();
        if (regions != null) {
            for (String region : regions) {
                newRegions.add(region.replace("%20", " "));
            }
        }

        PaginatedClientList clients = State.getClientManager().getClients(
                q,
                offset,
                count,
                minimumAge,
                maximumAge,
                newRegions,
                birthGenders,
                clientType,
                donating,
                requesting,
                sortOption,
                isReversed);

        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    /**
     * The POST endpoint for creating a new client
     *
     * @param createClientView The POJO representation of the create client view
     * @return Returns a Client overview. Also contains an ETag header for updates
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @PostMapping("/clients")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Client> createClient(
            @RequestBody CreateClientView createClientView,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws InvalidRequestException {

        //Validate the request, if there are any errors an exception will be thrown.
        if (!CreateClientValidator.isValid(createClientView)) {
            throw new InvalidRequestException();
        }

        //Create a new client with a default uid
        Client client = new Client();
        //Copy the details from the CreateClientView to the new Client object
        BeanUtils.copyProperties(createClientView, client);

        //Add the new Client to the manager
        CreateClientAction action = new CreateClientAction(client, State.getClientManager());
        ActionInvoker invoker = State.getActionInvoker(authToken);
        invoker.execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        return new ResponseEntity<>(client, headers, HttpStatus.CREATED);
    }

    /**
     * The single client GET endpoint
     *
     * @param uid The client UID to return
     * @return Returns a Client details object. Also contains an ETag header for updates
     */
    @GetMapping("/clients/{uid:\\d+}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Client> getClient(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            //Authenticate
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
            //Add the new ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.get().getETag());

            return new ResponseEntity<>(client.get(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * The PATCH endpoint for updating a single client
     *
     * @param uid                The client UID to update
     * @param modifyClientObject The POJO object of the modifications
     * @param etag               The corresponding If-Match header to check for concurrent update handling
     * @return Returns a Client overview. Also contains an ETag header for updates
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException   Thrown if the If-Match header does not match the Clients ETag. 412 error
     * @throws InvalidRequestException  Generic 400 exception if fields are malformed or inconsistent
     */
    @PatchMapping("/clients/{uid}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Client> updateClient(
            @PathVariable int uid,
            @RequestBody ModifyClientObject modifyClientObject,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
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
        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        //Check ETag
        checkClientETag(client, etag);

        //Validate the request, if there are any errors an exception will be thrown.
        if (!ModifyClientValidator.isValid(client, modifyClientObject)) {
            throw new InvalidRequestException();
        }

        //Do some extra validation now that we have the client object. Need to check if a date has been changed, it
        // will not become inconsistent
        if (!ClientBornAndDiedDatesValidator.isValid(modifyClientObject, client)) {
            throw new InvalidRequestException();
        }

        //Create the old details to allow undoable action
        ModifyClientObject oldClient = new ModifyClientObject();
        //Copy the values from the current client to our oldClient
        BeanUtils.copyProperties(client, oldClient, modifyClientObject.getUnmodifiedFields());

        //If client was not dead before but is now dead.
        MarkClientAsDeadAction markClientAsDeadAction = null;
        if (oldClient.getDateOfDeath() == null && modifyClientObject.getDateOfDeath() != null) {
            // Verify that the user sending this request is a clinician/admin (clients cannot mark themselves as dead)
            State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

            markClientAsDeadAction = new MarkClientAsDeadAction(client,
                    modifyClientObject.getDateOfDeath(),
                    modifyClientObject.getTimeOfDeath(),
                    modifyClientObject.getRegionOfDeath(),
                    modifyClientObject.getCityOfDeath(),
                    modifyClientObject.getCountryOfDeath(),
                    State.getClientManager());

            // Deregister death fields from the generic modify object (they will be updated by the mark dead action)
            modifyClientObject.deregisterChange("dateOfDeath");
            modifyClientObject.deregisterChange("timeOfDeath");
            modifyClientObject.deregisterChange("countryOfDeath");
            modifyClientObject.deregisterChange("regionOfDeath");
            modifyClientObject.deregisterChange("cityOfDeath");
        }

        if (!modifyClientObject.getModifiedFields().isEmpty()) {
            // Execute the action to make generic data changes
            ModifyClientByObjectAction action = new ModifyClientByObjectAction(client,
                    State.getClientManager(),
                    oldClient,
                    modifyClientObject);
            State.getActionInvoker(authToken).execute(action);
        }

        // Execute mark dead action if applicable
        if (markClientAsDeadAction != null) {
            State.getActionInvoker(authToken).execute(markClientAsDeadAction);
        }

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(client.getETag());

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(client, headers, HttpStatus.OK);
    }

    /**
     * The DELETE endpoint for removing a single client
     *
     * @param uid  The client UID to delete
     * @param etag The corresponding If-Match header to check for concurrent update handling
     * @return Returns an empty body with a simple response code
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException   Thrown if the If-Match header does not match the Clients ETag. 412 error
     * @throws InvalidRequestException  Generic 400 exception if fields are malformed or inconsistent
     */
    @DeleteMapping("/clients/{uid}")
    public ResponseEntity deleteClient(
            @PathVariable int uid,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        //Fetch the client given by ID
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            //Return 404 if that client does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Client client = optionalClient.get();

        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        //Check ETag
        checkClientETag(client, etag);

        DeleteClientAction action = new DeleteClientAction(client, State.getClientManager());
        State.getActionInvoker(authToken).execute(action);

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    /**
     * Returns the specified clients history
     *
     * @param uid       identifier of the client
     * @param authToken id token
     * @return The list of HistoryItems
     */
    @GetMapping("/clients/{uid}/history")
    public ResponseEntity<List<HistoryItem>> getHistory(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        Optional<Client> client = State.getClientManager().getClientByID(uid);
        if (client.isPresent()) {
            //Authenticate
            State.getAuthenticationManager().verifyClientAccess(authToken, client.get());
            //Add the new ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(client.get().getETag());

            return new ResponseEntity<>(client.get().getChangesHistory(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("clients/{uid}/image")
    public ResponseEntity<byte[]> getClientImage(
            @PathVariable int uid,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws InvalidRequestException, IfMatchFailedException, IfMatchRequiredException {

        // Check if the directory exists. If not, then clearly the image doesn't
        File directory = new File(State.getImageDirectory());
        if (!directory.exists()) {
            throw new NotFoundException();

        }

        // Get the relevant client
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        // Verify they are authenticated to access this client
        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        // Get image
        try (InputStream in = new FileInputStream(State.getImageDirectory() + uid + ".png")) {
            byte[] out = IOUtils.toByteArray(in);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            return new ResponseEntity<>(out, headers, HttpStatus.OK);
        } catch (FileNotFoundException ex) {
            throw new NotFoundException(ex);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("clients/{uid}/image")
    public ResponseEntity<?> postClientImage(
            @PathVariable int uid,
            @RequestBody byte[] image,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        // Create the directory if it doesn't exist
        File directory = new File(State.getImageDirectory());
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Get the relevant client
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client client = optionalClient.get();

        // Verify they are authenticated to access this client
        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        //Check ETag
        checkClientETag(client, etag);

        AddImageAction action = new AddImageAction(client, image, State.getImageDirectory());

        // Write the file
        try {
            State.getActionInvoker(authToken).execute(action);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (ImagingOpException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("clients/{uid}/image")
    public ResponseEntity<?> deleteClientImage(
            @PathVariable int uid,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) throws InvalidRequestException {

        // Check if the directory exists. If not, then clearly the image doesn't
        File directory = new File(State.getImageDirectory());
        if (!directory.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Get the relevant client
        Optional<Client> optionalClient = State.getClientManager().getClientByID(uid);
        if (!optionalClient.isPresent()) {
            throw new NotFoundException();
        }
        Client client = optionalClient.get();

        // Verify they are authenticated to access this client
        State.getAuthenticationManager().verifyClientAccess(authToken, client);

        //Check ETag
        checkClientETag(client, etag);

        try {
            DeleteImageAction action = new DeleteImageAction(client, State.getImageDirectory());
            State.getActionInvoker(authToken).execute(action);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
