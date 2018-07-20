package com.humanharvest.organz.utilities;

import com.humanharvest.organz.actions.Action;

/**
 * Observer for any Action events
 */
public interface ActionOccurredListener {

    void onActionExecuted(Action action);
    void onActionUndone(Action action);
    void onActionRedone(Action action);
}
