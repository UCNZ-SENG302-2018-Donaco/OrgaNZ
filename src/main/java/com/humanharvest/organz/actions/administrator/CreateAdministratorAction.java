package com.humanharvest.organz.actions.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.state.AdministratorManager;

/**
 * A reversible administrator creation action
 */
public class CreateAdministratorAction extends AdministratorAction {

    private final Administrator administrator;
    private final AdministratorManager manager;

    /**
     * Create a new Action
     * @param administrator The administrator to be created
     * @param manager The AdministratorManager to apply changes to
     */
    public CreateAdministratorAction(Administrator administrator, AdministratorManager manager) {
        this.administrator = administrator;
        this.manager = manager;
    }

    /**
     * Simply add the administrator to the AdministratorManager
     */
    @Override
    protected void execute() {
        super.execute();
        manager.addAdministrator(administrator);
    }

    /**
     * Simply remove the administrator from the AdministratorManager
     */
    @Override
    protected void unExecute() {
        super.unExecute();
        manager.removeAdministrator(administrator);
    }

    @Override
    public String getExecuteText() {
        return String.format("Created administrator %s", administrator.getUsername());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Removed administrator %s", administrator.getUsername());
    }

    @Override
    protected Administrator getAffectedAdministrator() {
        return administrator;
    }
}
