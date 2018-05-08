package seng302.Actions.Administrator;

import seng302.Actions.Action;
import seng302.Administrator;
import seng302.State.AdministratorManager;

/**
 * A reversible administrator deletion action
 */
public class DeleteAdministratorAction extends Action {

    private final Administrator administrator;
    private final AdministratorManager manager;

    /**
     * Create a new Action
     * @param administrator The Clinician to be removed
     * @param manager The ClinicianManager to apply changes to
     */
    public DeleteAdministratorAction(Administrator administrator, AdministratorManager manager) {
        this.administrator = administrator;
        this.manager = manager;
    }

    @Override
    protected void execute() {
        manager.removeAdministrator(administrator);
    }

    @Override
    protected void unExecute() {
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
}