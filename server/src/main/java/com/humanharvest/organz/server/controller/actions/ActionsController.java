package com.humanharvest.organz.server.controller.actions;

import com.humanharvest.organz.state.State;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActionsController {

    @PostMapping("/undo")
    public ResponseEntity<String> undoAction() {
        return new ResponseEntity<>(State.getInvoker().undo(), HttpStatus.OK);
    }

    @PostMapping("/redo")
    public ResponseEntity<String> redoAction() {
        return new ResponseEntity<>(State.getInvoker().undo(), HttpStatus.OK);
    }
}
