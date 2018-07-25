package com.humanharvest.organz.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.humanharvest.organz.utilities.ActionOccurredListener;

/**
 * The main invoker class for all model modifying actions. All actions should be using the Action implementation and
 * invoked by the invoker instance to allow undo/redo
 * Allows for action events to be observed by implementing the ActionOccurredListener then registering it with the
 * registerActionOccurredListener function
 */
public class ActionInvoker {

    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();
    private List<ActionOccurredListener> listeners = new ArrayList<>();

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

            listeners.forEach((listener -> listener.onActionUndone(action)));

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

            listeners.forEach((listener -> listener.onActionRedone(action)));

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

        listeners.forEach(listener -> listener.onActionExecuted(action));

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
     * Returns the next Action to be undone
     * @return The next Action that will be undone
     */
    public Action nextUndo() {
        return undoStack.peek();
    }

    /**
     * Returns the next Action to be redone
     * @return The next Action that will be redone
     */
    public Action nextRedo() {
        return redoStack.peek();
    }


    /**
     * Register a listener to be notified on any Action event
     * @param listener The listener to register
     */
    public void registerActionOccuredListener(ActionOccurredListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregister an action listener
     * @param listener The listener to unregister
     */
    public void unregisterActionOccuredListener(ActionOccurredListener listener) {
        listeners.remove(listener);
    }
}
