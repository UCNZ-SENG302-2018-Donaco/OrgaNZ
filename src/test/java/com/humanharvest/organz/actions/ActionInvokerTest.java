package com.humanharvest.organz.actions;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.humanharvest.organz.state.State;

import org.junit.Before;
import org.junit.Test;

public class ActionInvokerTest {

    private ActionInvoker invoker;
    private SettableItem item;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        item = new SettableItem();
        item.setString("Initial");
    }

    @Test
    public void BasicExecuteTest() {
        ModifySettableItemAction action = new ModifySettableItemAction(item, "New");

        invoker.execute(action);

        assertEquals("New", item.getString());
    }

    @Test
    public void BasicExecuteUndoTest() {
        ModifySettableItemAction action = new ModifySettableItemAction(item, "New");

        invoker.execute(action);
        invoker.undo();

        assertEquals("Initial", item.getString());
    }

    @Test
    public void BasicExecuteUndoRedoTest() {
        ModifySettableItemAction action = new ModifySettableItemAction(item, "New");

        invoker.execute(action);
        invoker.undo();
        invoker.redo();

        assertEquals("New", item.getString());
    }

    @Test
    public void CheckRedoIsResetOnNewExecuteTest() {
        ModifySettableItemAction action = new ModifySettableItemAction(item, "New");

        invoker.execute(action);
        invoker.undo();

        assertTrue(invoker.canRedo());

        invoker.execute(action);

        assertFalse(invoker.canRedo());
    }

    @Test
    public void TrackSaveStateTest() {
        ModifySettableItemAction action = new ModifySettableItemAction(item, "New");

        invoker.resetUnsavedUpdates();

        assertFalse(State.isUnsavedChanges());

        invoker.execute(action);

        assertTrue(State.isUnsavedChanges());

        invoker.undo();

        assertFalse(State.isUnsavedChanges());

        invoker.redo();

        assertTrue(State.isUnsavedChanges());

        invoker.resetUnsavedUpdates();

        assertFalse(State.isUnsavedChanges());
    }
}
