package com.humanharvest.organz.actions.client.organ;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.actions.client.ClientAction;
import com.humanharvest.organz.state.ClientManager;

public class CancelManualOverrideAction extends ClientAction {

    private DonatedOrgan donatedOrgan;
    private String overrideReason;

    public CancelManualOverrideAction(DonatedOrgan donatedOrgan, ClientManager manager) {
        super(donatedOrgan.getDonor(), manager);
        this.donatedOrgan = donatedOrgan;
        this.overrideReason = donatedOrgan.getOverrideReason();
    }

    @Override
    public void execute() {
        super.execute();
        donatedOrgan.cancelManualOverride();
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        donatedOrgan.manuallyOverride(overrideReason);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Cancelled the manual override on donated organ '%s' from client %d: %s.",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added the manual override on donated organ '%s' from donor %d: %s"
                        + "\nReason: '%s'",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName(), overrideReason);
    }
}
