package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.views.client.ModifyClientObject;
import org.springframework.beans.BeanUtils;

public class ModifyClientByObjectAction extends ClientAction {

    private ModifyClientObject oldClientDetails;
    private ModifyClientObject newClientDetails;

    /**
     * Create a new Action
     * @param client The client to be modified
     * @param manager The client manager to use when applying the changes.
     * @param oldClientDetails The object containing all the old details of the client record.
     * @param newClientDetails The object containing all the new details of the client record.
     */
    public ModifyClientByObjectAction(Client client, ClientManager manager, ModifyClientObject oldClientDetails,
            ModifyClientObject newClientDetails) {
        super(client, manager);
        this.oldClientDetails = oldClientDetails;
        this.newClientDetails = newClientDetails;
    }

    @Override
    protected void execute() {
        BeanUtils.copyProperties(newClientDetails, client, newClientDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        BeanUtils.copyProperties(oldClientDetails, client, oldClientDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return "Todo";
    }

    @Override
    public String getUnexecuteText() {
        return "Todo";
    }
}
