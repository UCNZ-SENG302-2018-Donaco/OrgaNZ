package com.humanharvest.organz.server.controller.actions;

import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActionsController {

    @PostMapping("/undo")
    public ResponseEntity<String> undoAction(@RequestHeader(value = "X-Auth-Token", required = false) String authToken) {
        return new ResponseEntity<>(State.getActionInvoker(authToken).undo(), HttpStatus.OK);
    }

    @PostMapping("/redo")
    public ResponseEntity<String> redoAction(@RequestHeader(value = "X-Auth-Token", required = false) String authToken) {
        return new ResponseEntity<>(State.getActionInvoker(authToken).undo(), HttpStatus.OK);
    }
}
