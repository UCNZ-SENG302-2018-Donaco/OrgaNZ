package seng302.Command;

import java.util.Stack;

public class CommandInvoker {
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    public void undo() {
        if (canUndo()) {
            Command command = undoStack.pop();
            command.unExecute();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (canRedo()) {
            Command command = redoStack.pop();
            command.unExecute();
            undoStack.push(command);
        }
    }

    public void execute(Command command) {
        command.execute();
        undoStack.push(command);
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

}
