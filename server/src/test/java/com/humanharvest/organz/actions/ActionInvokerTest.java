package com.humanharvest.organz.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.humanharvest.organz.BaseTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ActionInvokerTest extends BaseTest {

    private ActionInvoker invoker;
    private SettableItem item;

    @BeforeEach
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
}
