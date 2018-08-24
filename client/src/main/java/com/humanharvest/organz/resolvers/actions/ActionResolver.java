package com.humanharvest.organz.resolvers.actions;

import com.humanharvest.organz.views.ActionResponseView;

public interface ActionResolver {

    ActionResponseView executeUndo(String eTag);

    ActionResponseView executeRedo(String eTag);

    ActionResponseView getUndo();

}
