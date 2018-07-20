package com.humanharvest.organz.server.controller.administrator;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.actions.administrator.DeleteAdministratorAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.validators.administrator.CreateAdministratorValidator;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;
import com.humanharvest.organz.views.client.Views;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import picocli.CommandLine;

@RestController
public class AdministratorController {

    /**
     * Returns all administrators or some optional subset by filtering
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
     * @return A list of Administrator overviews
     * @throws AuthenticationException throws when a non-administrator attempts access.
     */
    @PostMapping("/administrators")
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

        administratorManager.addAdministrator(administrator);

        //Add the new ETag to the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setETag(administrator.getEtag());

        return new ResponseEntity<>(administrator, headers, HttpStatus.CREATED);
    }

    /**
     * Returns an administrator from the given username
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
            headers.setETag(administrator.get().getEtag());

            return new ResponseEntity<>(administrator.get(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * The DELETE endpoint for removing a single administrator
     * @param username The administrator username to delete
     * @param etag The corresponding If-Match header to check for concurrent update handling
     * @return Returns an empty body with a simple response code
     * @throws IfMatchRequiredException Thrown if there is no If-Match header, will result in a 428 error
     * @throws IfMatchFailedException Thrown if the If-Match header does not match the Administrators ETag. 412 error
     * @throws InvalidRequestException Generic 400 exception if fields are malformed or inconsistent
     */
    @DeleteMapping("/administrators/{username}")
    public ResponseEntity<Administrator> deleteAdministrator(
            @PathVariable String username,
            @RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication)
            throws IfMatchRequiredException, IfMatchFailedException, InvalidRequestException {

        State.getAuthenticationManager().verifyAdminAccess(authentication);

        AdministratorManager administratorManager = State.getAdministratorManager();

        //Fetch the administrator given by username
        Optional<Administrator> administrator = administratorManager.getAdministratorByUsername(username);
        if (!administrator.isPresent()) {
            //Return 404 if that administrator does not exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //Check the ETag. These are handled in the exceptions class.
        if (etag == null) {
            throw new IfMatchRequiredException("Etag does not exist");
        }
        if (!administrator.get().getEtag().equals(etag)) {
            throw new IfMatchFailedException("Etag is not valid for this administrator");
        }

        DeleteAdministratorAction action = new DeleteAdministratorAction(
                administrator.get(),
                administratorManager);
        State.getActionInvoker(authentication).execute(action);

        //Respond, apparently updates should be 200 not 201 unlike 365 and our spec
        return new ResponseEntity<>(administrator.get(), HttpStatus.OK);
    }

    @PostMapping("/sql")
    public ResponseEntity<String> executeSql(@RequestHeader(value = "If-Match", required = false) String etag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication) {
        //CommandLine.run();
        return null;
    }
}
