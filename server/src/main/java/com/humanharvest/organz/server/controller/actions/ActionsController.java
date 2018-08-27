package com.humanharvest.organz.server.controller.actions;

import java.util.EmptyStackException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.ConcurrencyControlledEntity;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.IfMatchFailedException;
import com.humanharvest.organz.views.ActionResponseView;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActionsController {

    private static final Logger LOGGER = Logger.getLogger(ActionsController.class.getName());

    @GetMapping("/undo")
    public ResponseEntity<ActionResponseView> getUndoActionText(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        //Get the next Action to undo
        ActionInvoker actionInvoker = State.getActionInvoker(authToken);

        String actionText;
        try {
            actionText = actionInvoker.nextUndo().getUnexecuteText();
        } catch (EmptyStackException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            actionText = "No more actions to undo";
        }
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();

        ActionResponseView responseView = new ActionResponseView(actionText, canUndo, canRedo);
        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }


    @PostMapping("/undo")
    public ResponseEntity<ActionResponseView> undoAction(
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        ActionInvoker actionInvoker = State.getActionInvoker(authToken);

        //Currently removed concurrency control for undo redo

        /*
        //Check that an ETag has been supplied
        if (ETag == null) throw new IfMatchRequiredException();


        //Get the next Action to undo
        Object modifiedObject;
        try {
            modifiedObject = actionInvoker.nextUndo().getModifiedObject();
        } catch (EmptyStackException e) {
            //If there are no more actions to undo, throw 404
            throw new NotFoundException();
        }

        checkETag(ETag, modifiedObject);
        */

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

        //Get the next Action to redo
        ActionInvoker actionInvoker = State.getActionInvoker(authToken);

        String actionText;
        try {
            actionText = actionInvoker.nextRedo().getExecuteText();
        } catch (EmptyStackException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            actionText = "No more actions to redo";
        }
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();

        ActionResponseView responseView = new ActionResponseView(actionText, canUndo, canRedo);
        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }

    @PostMapping("/redo")
    public ResponseEntity<ActionResponseView> redoAction(
            @RequestHeader(value = "If-Match", required = false) String ETag,
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken) {

        ActionInvoker actionInvoker = State.getActionInvoker(authToken);

        /* Currently removed concurrency control from undo redo

        //Check that an ETag has been supplied
        if (ETag == null) throw new IfMatchRequiredException();


        //Get the next Action to redo
        Object modifiedObject;
        try {
            modifiedObject = actionInvoker.nextRedo().getModifiedObject();
        } catch (EmptyStackException e) {
            //If there are no more actions to redo, throw 404
            throw new NotFoundException();
        }

        checkETag(ETag, modifiedObject);

        */

        //Execute the action
        String resultText = actionInvoker.redo();
        boolean canUndo = actionInvoker.canUndo();
        boolean canRedo = actionInvoker.canRedo();
        ActionResponseView responseView = new ActionResponseView(resultText, canUndo, canRedo);

        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }


    private static void checkETag(String ETag, Object modifiedObject) throws IfMatchFailedException {
        //Check the object matches the ETag
        //If the object is not a ConcurrencyControlledEntity, we will allow the action no matter what
        if (modifiedObject instanceof ConcurrencyControlledEntity) {
            if (!((ConcurrencyControlledEntity) modifiedObject).getETag().equals(ETag)) {
                throw new IfMatchFailedException("If-Match does not match");
            }
        }
    }
}
