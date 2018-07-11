package com.humanharvest.organz.server.controller.clinician;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.Views.Client.Views;
import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClinicianController {

    @GetMapping("/clinicians")
    @JsonView(Views.Overview.class)
    public ResponseEntity<List<Clinician>> getClinicians() {
        return new ResponseEntity<>(State.getClinicianManager().getClinicians(), HttpStatus.OK);
    }


}
