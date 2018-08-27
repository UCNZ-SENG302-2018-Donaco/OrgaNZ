package com.humanharvest.organz.server.controller.administrator;

import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.administrator.CreateAdministratorAction;
import com.humanharvest.organz.actions.administrator.DeleteAdministratorAction;
import com.humanharvest.organz.actions.administrator.ModifyAdministratorByObjectAction;
import com.humanharvest.organz.commands.CommandsHelper;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.validators.administrator.CreateAdministratorValidator;
import com.humanharvest.organz.utilities.validators.administrator.ModifyAdministratorValidator;
import com.humanharvest.organz.views.administrator.CommandView;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonView;
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
public class AdministratorController {

    /**
     * Returns all administrators or some optional subset by filtering
     *
     * @return A list of Administrator overviews
     * @throws AuthenticationException throws when a non-administrator attempts access.
     */
    @GetMapping("/administrators")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Iterable<Administrator>> getAdministrators(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer count,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication) {
        State.getAuthenticationManager().verifyAdminAccess(authentication);

        AdministratorManager administratorManager = State.getAdministratorManager();

        return new ResponseEntity<>(
                administratorManager.getAdministratorsFiltered(query, offset, count),
                HttpStatus.OK);
    }

    /**
     * Returns all administrators or some optional subset by filtering
     *
     * @return A list of Administrator overviews
     * @throws AuthenticationException throws when a non-administrator attempts access.
     */
    @PostMapping("/administrators")
    @JsonView(Views.Details.class)
    public ResponseEntity<Administrator> addAdministrator(
            @RequestBody CreateAdministratorView createAdministratorView,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication) {
        State.getAuthenticationManager().verifyAdminAccess(authentication);

        //Validate the request, if there are any errors an exception will be thrown.
        if (!CreateAdministratorValidator.isValid(createAdministratorView)) {
            throw new InvalidRequestException();
        }

        Administrator administrator = new Administrator(
                createAdministratorView.getUsername(),
                createAdministratorView.getPassword());

        AdministratorManager administratorManager = State.getAdministratorManager();
        CreateAdministratorAction action = new CreateAdministratorAction(administrator, administratorManager);
        ActionInvoker invoker = State.getActionInvoker(authentication);
        invoker.execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(administrator.getETag());
        System.out.println(administrator.getETag());

        return new ResponseEntity<>(administrator, headers, HttpStatus.CREATED);
    }

    /**
     * Returns an administrator from the given username
     *
     * @return An administrator detail view
     * @throws AuthenticationException throws when a non-administrator attempts access.
     */
    @GetMapping("/administrators/{username}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Administrator> getAdministrator(
            @PathVariable String username,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication) {
        State.getAuthenticationManager().verifyAdminAccess(authentication);

        AdministratorManager administratorManager = State.getAdministratorManager();
        Optional<Administrator> administrator = administratorManager.getAdministratorByUsername(username);
        if (administrator.isPresent()) {
            //Add the new ETag to the headers
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(administrator.get().getETag());

            return new ResponseEntity<>(administrator.get(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * The PATCH endpoint for updating a single administrator
     *
     * @param username The administrator username to update
     * @param modifyAdministratorObject The POJO object of the modifications
     * @param etag The corresponding If-Match header to check for concurrent update handling
     * @return Returns an Administrator overview. Also contains an ETag header for updates
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException Thrown if the If-Match header does not match the ETag. 412 error
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @PatchMapping("/administrators/{username}")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Administrator> updateAdministrator(
            @PathVariable String username,
            @RequestBody ModifyAdministratorObject modifyAdministratorObject,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException, AuthenticationException {

        //Logical steps for a PATCH
        //We set If-Match to false so we can return a better error code than 400 which happens if a required
        // @RequestHeader is missing, I think this can be improved with an @ExceptionHandler or similar so we don't
        // duplicate code in tons of places but need to work it out

        //Fetch the administrator given by username
        Optional<Administrator> optionalAdministrator =
                State.getAdministratorManager().getAdministratorByUsername(username);
        if (!optionalAdministrator.isPresent()) {
            //Return 404 if that administrator does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Administrator administrator = optionalAdministrator.get();

        //Check authentication
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        //Validate the request, if there are any errors an exception will be thrown.
        if (!ModifyAdministratorValidator.isValid(modifyAdministratorObject)) {
            throw new InvalidRequestException();
        }

        //Check the ETag. These are handled in the exceptions class.
        if (etag == null) {
            throw new IfMatchRequiredException();
        }

        if (!administrator.getETag().equals(etag)) {
            throw new IfMatchFailedException();
        }

        //Create the old details to allow undoable action
        ModifyAdministratorObject oldClient = new ModifyAdministratorObject();
        //Copy the values from the current client to our oldClient
        BeanUtils.copyProperties(administrator, oldClient, modifyAdministratorObject.getUnmodifiedFields());
        //Make the action (this is a new action)
        ModifyAdministratorByObjectAction action = new ModifyAdministratorByObjectAction(administrator,
                State.getAdministratorManager(),
                oldClient,
                modifyAdministratorObject);
        //Execute action, this would correspond to a specific users invoker in full version
        State.getActionInvoker(authToken).execute(action);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(administrator.getETag());

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(administrator, headers, HttpStatus.OK);
    }

    /**
     * The DELETE endpoint for removing a single administrator
     *
     * @param username The administrator username to delete
     * @param etag The corresponding If-Match header to check for concurrent update handling
     * @return Returns an empty body with a simple response code
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException Thrown if the If-Match header does not match the Administrators ETag. 412 error
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @DeleteMapping("/administrators/{username}")
    public ResponseEntity<?> deleteAdministrator(
            @PathVariable String username,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        State.getAuthenticationManager().verifyAdminAccess(authentication);

        AdministratorManager administratorManager = State.getAdministratorManager();

        //Fetch the administrator given by username
        Optional<Administrator> optionalAdministrator =
                State.getAdministratorManager().getAdministratorByUsername(username);
        if (!optionalAdministrator.isPresent()) {
            //Return 404 if that administrator does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Administrator administrator = optionalAdministrator.get();

        if (administrator.equals(State.getAdministratorManager().getDefaultAdministrator())) {
            return new ResponseEntity<>("Unable to delete the default administrator.", HttpStatus.BAD_REQUEST);
        }

        //Check the ETag. These are handled in the exceptions class.
        if (etag == null) {
            throw new IfMatchRequiredException("Etag does not exist");
        }
        if (!administrator.getETag().equals(etag)) {
            throw new IfMatchFailedException("Etag is not valid for this administrator");
        }

        DeleteAdministratorAction action = new DeleteAdministratorAction(
                administrator,
                administratorManager);
        State.getActionInvoker(authentication).execute(action);

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(administrator, HttpStatus.OK);
    }

    /**
     * Allows admin commands to be run via the server
     *
     * @param commandText The text object to execute
     * @param authToken The authentication token of a valid administrator
     * @return The result string of a command execution
     */
    @PostMapping("/commands")
    public ResponseEntity<String> executeCommands(
            @RequestBody CommandView commandText,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {
        //Check valid admin
        State.getAuthenticationManager().verifyAdminAccess(authToken);
        //Get the action invoker for the admin
        ActionInvoker invoker = State.getActionInvoker(authToken);

        String[] commands = CommandsHelper.parseCommands(commandText.getCommand());

        String result = CommandsHelper.executeCommandAndReturnOutput(commands, invoker);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Returns some history for all actions
     *
     * @param authToken id token
     * @return The list of HistoryItems
     */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryItem>> getHistory(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        State.getAuthenticationManager().verifyAdminAccess(authToken);

        return new ResponseEntity<>(State.getClientManager().getAllHistoryItems(), HttpStatus.OK);
    }
}
