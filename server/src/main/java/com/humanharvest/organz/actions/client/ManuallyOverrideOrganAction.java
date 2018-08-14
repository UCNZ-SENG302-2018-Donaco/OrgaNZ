package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.state.ClientManager;

public class ManuallyOverrideOrganAction extends ClientAction {

    private DonatedOrgan donatedOrgan;
    private String overrideReason;

    public ManuallyOverrideOrganAction(DonatedOrgan donatedOrgan, String overrideReason, ClientManager manager) {
        super(donatedOrgan.getDonor(), manager);
        this.donatedOrgan = donatedOrgan;
        this.overrideReason = overrideReason;
    }

    @Override
    public void execute() {
        super.execute();
        donatedOrgan.manuallyOverride(overrideReason);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        donatedOrgan.cancelManualOverride();
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Manually overrode donated organ '%s' from donor %d: %s"
                        + "\nReason: '%s'",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName(), overrideReason);
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the manual override on donated organ '%s' from client %d: %s.",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName());
    }
}
