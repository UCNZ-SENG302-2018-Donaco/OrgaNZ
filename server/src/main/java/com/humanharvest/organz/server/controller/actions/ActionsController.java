package com.humanharvest.organz.server.controller.actions;

import java.util.EmptyStackException;

import com.humanharvest.organz.ConcurrencyControlledEntity;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.utilities.exceptions.IfMatchRequiredException;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.views.ActionResponseView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActionsController {

    @GetMapping("/undo")
    public ResponseEntity<ActionResponseView> getUndoActionText(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        //Check is valid Admin
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        //Get the next Action to undo
        ActionInvoker actionInvoker = State.getActionInvoker(authToken);

        String actionText = actionInvoker.nextUndo().getUnexecuteText();
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();

        ActionResponseView responseView = new ActionResponseView(actionText, canUndo, canRedo);
        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }


    @PostMapping("/undo")
    public ResponseEntity<ActionResponseView> undoAction(
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        basicChecks(ETag, authToken);

        //Get the next Action to undo
        ActionInvoker actionInvoker = State.getActionInvoker(authToken);
        Object modifiedObject;
        try {
            modifiedObject = actionInvoker.nextUndo().getModifiedObject();
        } catch (EmptyStackException e) {
            //If there are no more actions to undo, throw 404
            throw new NotFoundException();
        }

        checkETag(ETag, modifiedObject);

        //Execute the action
        String resultText = actionInvoker.undo();
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();
        ActionResponseView responseView = new ActionResponseView(resultText, canUndo, canRedo);

        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }

    @GetMapping("/redo")
    public ResponseEntity<ActionResponseView> getRedoActionText(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        //Check is valid Admin
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        //Get the next Action to redo
        ActionInvoker actionInvoker = State.getActionInvoker(authToken);

        String actionText = actionInvoker.nextRedo().getExecuteText();
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();

        ActionResponseView responseView = new ActionResponseView(actionText, canUndo, canRedo);
        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }

    @PostMapping("/redo")
    public ResponseEntity<ActionResponseView> redoAction(
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        basicChecks(ETag, authToken);

        //Get the next Action to redo
        ActionInvoker actionInvoker = State.getActionInvoker(authToken);
        Object modifiedObject;
        try {
            modifiedObject = actionInvoker.nextRedo().getModifiedObject();
        } catch (EmptyStackException e) {
            //If there are no more actions to redo, throw 404
            throw new NotFoundException();
        }

        checkETag(ETag, modifiedObject);

        //Execute the action
        String resultText = actionInvoker.redo();
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();
        ActionResponseView responseView = new ActionResponseView(resultText, canUndo, canRedo);

        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }




    private void basicChecks(String ETag, String authToken) throws AuthenticationException, IfMatchFailedException {
        //Check is valid Admin
        State.getAuthenticationManager().verifyAdminAccess(authToken);

        //Check that an ETag has been supplied
        if (ETag == null) throw new IfMatchRequiredException();
    }

    private void checkETag(String ETag, Object modifiedObject) throws IfMatchFailedException {
        //Check the object matches the ETag
        //If the object is not a ConcurrencyControlledEntity, we will allow the action no matter what
        if (modifiedObject instanceof ConcurrencyControlledEntity) {
            if (!((ConcurrencyControlledEntity) modifiedObject).getETag().equals(ETag)) {
                throw new IfMatchFailedException("If-Match does not match");
            }
        }
    }
}
