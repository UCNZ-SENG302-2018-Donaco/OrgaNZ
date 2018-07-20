package com.humanharvest.organz.actions;

import java.util.Stack;

/**
 * The main invoker class for all model modifying actions. All actions should be using the Action implementation and
 * invoked by the invoker instance to allow undo/redo
 */
public class ActionInvoker {

    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();
    private int unsavedUpdates = 0;

    /**
     * Undo the last action
     * @return Returns a string description of the action. Used for notifications, or null if there was no actions to
     * undo
     */
    public String undo() {
        if (canUndo()) {
            Action action = undoStack.pop();
            action.unExecute();
            redoStack.push(action);
            unsavedUpdates--;
            //todo I commented out the below line MERGECONFLICT
            //State.setUnsavedChanges(unsavedUpdates != 0);
            return action.getUnexecuteText();
        }
        return "No more actions to undo";
    }

    /**
     * Redo the last action
     * @return Returns a string description of the action. Used for notifications, or null if there was no actions to
     * redo
     */
    public String redo() {
        if (canRedo()) {
            Action action = redoStack.pop();
            action.execute();
            undoStack.push(action);
            unsavedUpdates++;
            //todo I commented out the below line MERGECONFLICT
            //State.setUnsavedChanges(unsavedUpdates != 0);
            return action.getExecuteText();
        }
        return "No more actions to redo";
    }

    /**
     * Pass an action object to be executed. Adds the action to the undo list.
     * @param action An object implementing the Action interface
     * @return Returns a string description of the action. Used for notifications.
     */
    public String execute(Action action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();
        unsavedUpdates++;
        //todo I commented out the below line MERGECONFLICT
        //State.setUnsavedChanges(true);
        return action.getExecuteText();
    }

    /**
     * Checks if there are any actions in the undo stack
     * @return Are there any actions do be undone
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Checks if there are any actions in the redo stack
     * @return Are there any actions do be redone
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Reset the UnsavedUpdates counter, should be used when the state is saved
     */
    public void resetUnsavedUpdates() {
        unsavedUpdates = 0;
        //todo I commented out the below line MERGECONFLICT
        //State.setUnsavedChanges(false);
    }
}
