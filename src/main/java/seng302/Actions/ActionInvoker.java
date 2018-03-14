package seng302.Actions;

import java.util.Stack;

public class ActionInvoker {
    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();

    public void undo() {
        if (canUndo()) {
            Action action = undoStack.pop();
            action.unExecute();
            redoStack.push(action);
        }
    }

    public void redo() {
        if (canRedo()) {
            Action action = redoStack.pop();
            action.unExecute();
            undoStack.push(action);
        }
    }

    public void execute(Action action) {
        action.execute();
        undoStack.push(action);
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

}
