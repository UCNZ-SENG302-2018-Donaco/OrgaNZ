package com.humanharvest.organz.server.controller.clinician;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.utilities.validators.clinician.CreateClinicianValidator;
import com.humanharvest.organz.views.client.Views;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClinicianController {

    /**
     * The GET /clinicians endpoint which returns all clinicians in the system.
     * @return all clinicians
     */
    @GetMapping("/clinicians")
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Clinician>> getClinicians(@RequestHeader(value="X-Auth-Token", required=false) String authToken) {
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
    public ResponseEntity<Clinician> createClinician(@RequestBody Clinician clinician, @RequestHeader
            (value="X-Auth-Token", required=false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {
        if (!CreateClinicianValidator.isValid(clinician)) {
            throw new GlobalControllerExceptionHandler.InvalidRequestException();
        }

        if (State.getClinicianManager().doesStaffIdExist(clinician.getStaffId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        State.getAuthenticationManager().verifyAdminAccess(authToken);
        State.getClinicianManager().addClinician(clinician);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(clinician.getEtag());
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
            (value="X-Auth-Token", required=false) String authToken) {

        State.getAuthenticationManager().verifyAdminAccess(authToken);
        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        if (clinician.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(clinician.get().getEtag());
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
    public ResponseEntity<Clinician> editClinician(@PathVariable int staffId, @RequestBody Optional<Clinician>
            editedClinician ,
            @RequestHeader(value="X-Auth-Token", required=false) String authToken) {

        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        State.getAuthenticationManager().vefifyClinicianAccess(authToken, clinician.get());

        if (clinician.isPresent()) {

            if (!CreateClinicianValidator.isValid(editedClinician.get())) {
                throw new GlobalControllerExceptionHandler.InvalidRequestException();

            } else {
                //Need to create patch logic
                State.getClinicianManager().applyChangesTo(editedClinician.get());
                HttpHeaders headers = new HttpHeaders();
                return new ResponseEntity<>(clinician.get(), headers, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @DeleteMapping("/clinicians/{staffId}")
    public ResponseEntity deleteClient(@PathVariable int staffId, @RequestHeader(value="X-Auth-Token", required=false) String authToken) {
        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        if (staffId == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (clinician.isPresent()) {
            State.getClinicianManager().removeClinician(clinician.get());
            return new ResponseEntity(HttpStatus.OK);
            // else 403
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
