package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.state.ClientManager;

public class DeleteDonatedOrganAction extends ClientAction{

    private DonatedOrgan donatedOrgan;

    public DeleteDonatedOrganAction(Client client,DonatedOrgan donatedOrgan,ClientManager manager) {
        super(client, manager);
        this.donatedOrgan = donatedOrgan;
    }

    @Override
    public void execute(){
        super.execute();
        client.deleteDonatedOrgan(donatedOrgan);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute(){
        super.unExecute();
        client.addDonatedOrgan(donatedOrgan);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText(){
        return String.format("Removed donated organ '%s' from client %d: %s.",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText(){
        return String.format("Re-Added donated organ '%s' to client %d: %s.",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName());
    }


}
