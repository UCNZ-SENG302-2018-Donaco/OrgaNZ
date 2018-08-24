package com.humanharvest.organz.resolvers.actions;

import com.humanharvest.organz.views.ActionResponseView;

public class ActionResolverMemory implements ActionResolver {

    @Override
    public ActionResponseView executeUndo(String eTag) {
        return new ActionResponseView("Faked", false, false);
    }

    @Override
    public ActionResponseView executeRedo(String eTag) {
        return new ActionResponseView("Faked", false, false);
    }

    @Override
    public ActionResponseView getUndo() {
        return new ActionResponseView("Faked", false, false);
    }
}
