package com.humanharvest.organz.server.controller.clinician;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.serialisation.JSONFileWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides handlers for requests to these endpoints:
 * - GET /clincians/file
 * All endpoints require administrator access.
 */
@RestController
public class ClinicianFileController {

    @GetMapping("/clinicians/file")
    public ResponseEntity<byte[]> exportClinicians(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws AuthenticationException, IOException {

        // Check request has authorization to export all clinicians
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JSONFileWriter<Clinician> clinicianWriter = new JSONFileWriter<>(output, Clinician.class)) {
            clinicianWriter.overwriteWith(State.getClinicianManager().getClinicians());
        }

        return new ResponseEntity<>(output.toByteArray(), HttpStatus.OK);
    }
}
