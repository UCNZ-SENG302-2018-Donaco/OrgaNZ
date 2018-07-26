package com.humanharvest.organz.server.controller.clinician;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.clinician.CreateClinicianAction;
import com.humanharvest.organz.actions.clinician.DeleteClinicianAction;
import com.humanharvest.organz.actions.clinician.ModifyClinicianByObjectAction;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.validators.clinician.CreateClinicianValidator;
import com.humanharvest.organz.utilities.validators.clinician.ModifyClinicianValidator;
import com.humanharvest.organz.views.client.Views;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
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
public class ClinicianController {

    /**
     * The GET /clinicians endpoint which returns all clinicians in the system.
     * @return all clinicians
     */
    @GetMapping("/clinicians")
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Clinician>> getClinicians(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {
        State.getAuthenticationManager().verifyAdminAccess(authToken);
        return new ResponseEntity<>(State.getClinicianManager().getClinicians(), HttpStatus.OK);
    }

    /**
     * The POST /clinicians endpoint which creates a clinician from the request body parameters.
     * @param clinician details of the clinician being posted
     * @return a detailed view of the newly created clinician
     * @throws GlobalControllerExceptionHandler.InvalidRequestException When invalid parameters are given
     */
    @PostMapping("/clinicians")
    @JsonView(Views.Details.class)
    public ResponseEntity<Clinician> createClinician(
            @RequestBody Clinician clinician,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        if (!CreateClinicianValidator.isValid(clinician)) {
            throw new GlobalControllerExceptionHandler.InvalidRequestException();
        }

        if (State.getClinicianManager().doesStaffIdExist(clinician.getStaffId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        CreateClinicianAction action = new CreateClinicianAction(clinician, State.getClinicianManager());
        ActionInvoker invoker = State.getActionInvoker(authToken);
        invoker.execute(action);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(clinician.getETag());
        return new ResponseEntity<>(clinician, headers, HttpStatus.CREATED);
    }

    /**
     * The GET /clinicians/{staffId} endpoint which returns the specified clinicians details
     * @param staffId the id of the clinician
     * @return the details of the specified clinician
     */
    @GetMapping("/clinicians/{staffId}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Clinician> getCliniciansById(@PathVariable int staffId, @RequestHeader
            (value = "X-Auth-Token", required = false) String authToken) {

        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        if (clinician.isPresent()) {
            State.getAuthenticationManager().verifyClinicianAccess(authToken, clinician.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(clinician.get().getETag());
            return new ResponseEntity<>(clinician.get(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Edits the details of the specified clinician. Note that the staffId cannot be changed.
     * @param staffId identifier of the clinician
     * @param editedClinician the body containing all updated information
     * @return response status
     */
    @PatchMapping("/clinicians/{staffId}")
    @JsonView(Views.Details.class)
    public ResponseEntity<Clinician> editClinician(
            @PathVariable int staffId,
            @RequestBody ModifyClinicianObject editedClinician,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);


        if (editedClinician.getStaffId() != staffId) {
            // Cannot patch the unique id
            throw new GlobalControllerExceptionHandler.InvalidRequestException();
        }

        if (clinician.isPresent()) {
            State.getAuthenticationManager().verifyClinicianAccess(authToken, clinician.get());

            if (ModifyClinicianValidator.isValid(editedClinician)) {

                ModifyClinicianObject oldClinician = new ModifyClinicianObject();
                BeanUtils.copyProperties(editedClinician, oldClinician, editedClinician.getUnmodifiedFields());
                ModifyClinicianByObjectAction action = new ModifyClinicianByObjectAction(clinician.get(),
                        State.getClinicianManager(),
                        oldClinician, editedClinician);
                State.getActionInvoker(authToken).execute(action);

                State.getClinicianManager().applyChangesTo(clinician.get());
                HttpHeaders headers = new HttpHeaders();
                return new ResponseEntity<>(clinician.get(), headers, HttpStatus.OK);
            } else {
                throw new GlobalControllerExceptionHandler.InvalidRequestException();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes the specified clinician
     * @param staffId identifier of the clinician
     * @param authToken id token
     */
    @DeleteMapping("/clinicians/{staffId}")
    public ResponseEntity<Clinician> deleteClinician(
            @PathVariable int staffId,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        if (staffId == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (clinician.isPresent()) {
            DeleteClinicianAction action = new DeleteClinicianAction(clinician.get(), State.getClinicianManager());
            ActionInvoker invoker = State.getActionInvoker(authToken);
            invoker.execute(action);
            return new ResponseEntity<>(HttpStatus.CREATED);
            // else 403
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Returns the specified clinicians history
     * @param staffId identifier of the clinician
     * @param authToken id token
     * @return The list of HistoryItems
     */
    @GetMapping("/clinicians/{staffId}/history")
    public ResponseEntity<List<HistoryItem>> getHistory(
            @PathVariable int staffId,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        if (clinician.isPresent()) {
            State.getAuthenticationManager().verifyClinicianAccess(authToken, clinician.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(clinician.get().getETag());
            return new ResponseEntity<>(clinician.get().getChangesHistory(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
