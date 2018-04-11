package seng302.Actions;

import java.util.Stack;

import seng302.State.State;

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
            State.setUnsavedChanges(unsavedUpdates != 0);
            return action.getUnexecuteText();
        }
        return null;
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
            State.setUnsavedChanges(unsavedUpdates != 0);
            return action.getExecuteText();
        }
        return null;
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
        State.setUnsavedChanges(true);
        return action.getUnexecuteText();
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
        State.setUnsavedChanges(false);
    }
}
