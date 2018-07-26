package com.humanharvest.organz.server.controller.authentication;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.administrator.AdministratorLoginRequest;
import com.humanharvest.organz.views.administrator.AdministratorLoginResponse;
import com.humanharvest.organz.views.client.ClientLoginRequest;
import com.humanharvest.organz.views.client.ClientLoginResponse;
import com.humanharvest.organz.views.client.Views;
import com.humanharvest.organz.views.clinician.ClinicianLoginRequest;
import com.humanharvest.organz.views.clinician.ClinicianLoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    /**
     * Logs in a client.
     */
    @PostMapping("/login/client")
    @JsonView(Views.Details.class)
    public ResponseEntity<ClientLoginResponse> loginClient(
            @RequestBody ClientLoginRequest loginRequest,
            @RequestParam(required = false) String view) {

        int id = loginRequest.getId();
        ClientManager clientManager = State.getClientManager();
        Optional<Client> client = clientManager.getClientByID(id);

        if (!client.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = State.getAuthenticationManager().generateClientToken(client.get().getUid());

        ClientLoginResponse loginResponse =
                "full".equals(view) || view == null ?
                        new ClientLoginResponse(token, client.get()) :
                        new ClientLoginResponse(token);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    /**
     * Logs in a clinician.
     */
    @PostMapping("/login/clinician")
    @JsonView(Views.Details.class)
    public ResponseEntity<ClinicianLoginResponse> loginClinician(
            @RequestBody ClinicianLoginRequest loginRequest,
            @RequestParam(required = false) String view) {

        int staffId = loginRequest.getStaffId();
        ClinicianManager clinicianManager = State.getClinicianManager();
        Optional<Clinician> clinician = clinicianManager.getClinicianByStaffId(staffId);

        if (!clinician.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!clinician.get().isPasswordValid(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = State.getAuthenticationManager().generateClinicianToken(clinician.get().getStaffId());

        ClinicianLoginResponse loginResponse =
                "full".equals(view) || view == null ?
                        new ClinicianLoginResponse(token, clinician.get()) :
                        new ClinicianLoginResponse(token);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    /**
     * Logs in an administrator.
     */
    @PostMapping("/login/administrator")
    @JsonView(Views.Details.class)
    public ResponseEntity<AdministratorLoginResponse> loginAdministrator(
            @RequestBody AdministratorLoginRequest loginRequest,
            @RequestParam(required = false) String view) {

        String username = loginRequest.getUsername();
        AdministratorManager administratorManager = State.getAdministratorManager();
        Optional<Administrator> administrator = administratorManager.getAdministratorByUsername(username);

        if (!administrator.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!administrator.get().isPasswordValid(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = State.getAuthenticationManager().generateAdministratorToken(administrator.get().getUsername());

        AdministratorLoginResponse loginResponse =
                "full".equals(view) || view == null ?
                        new AdministratorLoginResponse(token, administrator.get()) :
                        new AdministratorLoginResponse(token);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
