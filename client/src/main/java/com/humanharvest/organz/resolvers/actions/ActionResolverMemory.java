package com.humanharvest.organz.resolvers.actions;

import com.humanharvest.organz.views.ActionResponseView;

public class ActionResolverMemory implements ActionResolver {

    public ActionResponseView executeUndo(String ETag) {
        return new ActionResponseView("Faked", false, false);
    }

    public ActionResponseView executeRedo(String ETag) {
        return new ActionResponseView("Faked", false, false);
    }

    public ActionResponseView getUndo() {
        return new ActionResponseView("Faked", false, false);
    }
}
