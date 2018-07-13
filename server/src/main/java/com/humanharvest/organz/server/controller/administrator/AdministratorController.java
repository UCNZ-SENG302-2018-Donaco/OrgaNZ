package com.humanharvest.organz.server.controller.administrator;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Views.Client.Views;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministratorController {

    /**
     * Returns all administrators or some optional subset by filtering
     * @return A list of Administrator overviews
     */
    @GetMapping("/administrators")
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Administrator>> getAdministrators() {
        //TODO: Auth
        //TODO: Filters
        return new ResponseEntity<>(State.getAdministratorManager().getAdministrators(), HttpStatus.OK);
    }
}
