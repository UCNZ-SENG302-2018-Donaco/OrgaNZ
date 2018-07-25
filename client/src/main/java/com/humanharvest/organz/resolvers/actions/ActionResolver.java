package com.humanharvest.organz.resolvers.actions;

import com.humanharvest.organz.views.ActionResponseView;

public interface ActionResolver {

    ActionResponseView executeUndo(String ETag);

    ActionResponseView executeRedo(String ETag);

    ActionResponseView getUndo();

}
