package com.humanharvest.organz.server.controller.clinician;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
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
    public ResponseEntity<List<Clinician>> getClinicians() {
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
    public ResponseEntity<Clinician> createClinician(@RequestBody Clinician clinician) throws GlobalControllerExceptionHandler.InvalidRequestException {
        if (!CreateClinicianValidator.isValid(clinician)) {
            throw new GlobalControllerExceptionHandler.InvalidRequestException();
        }

        if (State.getClinicianManager().doesStaffIdExist(clinician.getStaffId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<Clinician> getCliniciansById(@PathVariable int staffId) {


        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        if (clinician.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
//            headers.setETag(clinician.getEtag());
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
    public ResponseEntity<Clinician> editClinician(@PathVariable int staffId, @RequestBody Clinician editedClinician) {
        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        if (clinician.isPresent()) {
            if (!CreateClinicianValidator.isValid(editedClinician)) {
                throw new GlobalControllerExceptionHandler.InvalidRequestException();

            } else {
                //Need to create patch logic

                //clinician = editedClinician;

                HttpHeaders headers = new HttpHeaders();
                return new ResponseEntity<>(clinician.get(), headers, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


    @DeleteMapping("/clinicians/{staffId}")
    public ResponseEntity deleteClient(@PathVariable int staffId) {
        Optional<Clinician> clinician = State.getClinicianManager().getClinicianByStaffId(staffId);
        if (clinician.isPresent()) {
            // if authorised
            // can the default clinician be removed?
            // Delete clinician
            State.getClinicianManager().removeClinician(clinician.get());
            return new ResponseEntity<>(HttpStatus.OK);
            // else 403

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
