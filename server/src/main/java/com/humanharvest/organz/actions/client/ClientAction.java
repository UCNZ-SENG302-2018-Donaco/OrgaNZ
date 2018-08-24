package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;

public abstract class ClientAction extends Action {

    final Client client;
    final ClientManager manager;

    protected ClientAction(Client client, ClientManager manager) {
        this.client = client;
        this.manager = manager;
    }

    @Override
    protected void execute() {
        recordInClientHistory();
    }

    @Override
    protected void unExecute() {
        eraseFromClientHistory();
    }

    @Override
    public Object getModifiedObject() {
        return client;
    }

    private void recordInClientHistory() {
        client.addToChangesHistory(getExecuteHistoryItem().copy());
    }

    private void eraseFromClientHistory() {
        client.removeFromChangesHistory(getExecuteHistoryItem());
    }
}
