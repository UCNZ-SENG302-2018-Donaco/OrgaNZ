package com.humanharvest.organz.server.controller.administrator;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.views.client.Views;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministratorController {

    /**
     * Returns all administrators or some optional subset by filtering
     * @return A list of Administrator overviews
     */
    @GetMapping("/administrators")
    @JsonView(Views.Overview.class)
    public ResponseEntity<Iterable<Administrator>> getAdministrators(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer count,
            @RequestHeader(value = "X-Auth-Token", required = false) String authentication)
            throws AuthenticationException {
        State.getAuthenticationManager().verifyAdminAccess(authentication);

        AdministratorManager administratorManager = State.getAdministratorManager();

        return new ResponseEntity<>(
                administratorManager.getAdministratorsFiltered(query, offset, count),
                HttpStatus.OK);
    }
}
