package com.humanharvest.organz.actions.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.state.AdministratorManager;

/**
 * A reversible administrator deletion action
 */
public class DeleteAdministratorAction extends AdministratorAction {

    private final Administrator administrator;
    private final AdministratorManager manager;

    /**
     * Create a new Action
     * @param administrator The Administrator to be removed
     * @param manager The AdministratorManager to apply changes to
     */
    public DeleteAdministratorAction(Administrator administrator, AdministratorManager manager) {
        this.administrator = administrator;
        this.manager = manager;
    }

    @Override
    protected void execute() {
        super.execute();
        manager.removeAdministrator(administrator);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        manager.addAdministrator(administrator);
    }

    @Override
    public String getExecuteText() {
        return String.format("Deleted administrator %s", administrator.getUsername());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added administrator %s", administrator.getUsername());
    }

    @Override
    protected Administrator getAffectedAdministrator() {
        return administrator;
    }
}