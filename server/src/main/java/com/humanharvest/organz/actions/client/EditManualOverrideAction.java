package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.state.ClientManager;

public class EditManualOverrideAction extends ClientAction {

    private DonatedOrgan donatedOrgan;
    private String oldOverrideReason;
    private String newOverrideReason;

    public EditManualOverrideAction(DonatedOrgan donatedOrgan, String newOverrideReason, ClientManager manager) {
        super(donatedOrgan.getDonor(), manager);
        this.donatedOrgan = donatedOrgan;
        this.oldOverrideReason = donatedOrgan.getOverrideReason();
        this.newOverrideReason = newOverrideReason;
    }

    @Override
    public void execute() {
        super.execute();
        donatedOrgan.manuallyOverride(newOverrideReason);
        manager.applyChangesTo(client);
    }

    @Override
    public void unExecute() {
        super.unExecute();
        donatedOrgan.manuallyOverride(oldOverrideReason);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Changed the reason for manual override on donated organ '%s' from donor %d: %s"
                        + "\nOld Reason: '%s'"
                        + "\nNew Reason: '%s'",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName(),
                oldOverrideReason, newOverrideReason);
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the change in manual override on donated organ '%s' from donor %d: %s"
                        + "\nOld Reason: '%s'"
                        + "\nNew Reason: '%s'",
                donatedOrgan.getOrganType(), client.getUid(), client.getFullName(),
                oldOverrideReason, newOverrideReason);
    }
}
