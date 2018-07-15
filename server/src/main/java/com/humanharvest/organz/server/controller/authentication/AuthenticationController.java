package com.humanharvest.organz.server.controller.authentication;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.administrator.AdministratorLoginRequest;
import com.humanharvest.organz.views.administrator.AdministratorLoginResponse;
import com.humanharvest.organz.views.client.Views;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    /**
     * Logs in an administrator.
     */
    @PostMapping("/login/administrator")
    @JsonView(Views.Overview.class)
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
                ("full".equals(view) || view == null) ?
                new AdministratorLoginResponse(token, administrator.get()) :
                new AdministratorLoginResponse(token);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
