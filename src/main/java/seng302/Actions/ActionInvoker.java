package seng302.Actions;

import java.util.Stack;

/**
 * The main invoker class for all model modifying actions. All actions should be using the Action implementation and
 * invoked by the invoker instance to allow undo/redo
 */
public class ActionInvoker {

    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();

    /**
     * Undo the last action
     */
    public void undo() {
        if (canUndo()) {
            Action action = undoStack.pop();
            action.unExecute();
            redoStack.push(action);
        }
    }

    /**
     * Redo the last undone action
     */
    public void redo() {
        if (canRedo()) {
            Action action = redoStack.pop();
            action.execute();
            undoStack.push(action);
        }
    }

    /**
     * Pass an action object to be executed. Adds the action to the undo list.
     * @param action An object implementing the Action interface
     */
    public void execute(Action action) {
        action.execute();
        undoStack.push(action);
        redoStack.empty();
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

}
