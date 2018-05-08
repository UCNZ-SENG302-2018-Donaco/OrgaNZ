package seng302.Actions.Administrator;

import seng302.Actions.Action;
import seng302.Administrator;
import seng302.State.AdministratorManager;

/**
 * A reversible administrator creation action
 */
public class CreateAdministratorAction extends Action {

    private final Administrator administrator;
    private final AdministratorManager manager;

    /**
     * Create a new Action
     * @param administrator The Clinician to be created
     * @param manager The ClinicianManager to apply changes to
     */
    public CreateAdministratorAction(Administrator administrator, AdministratorManager manager) {
        this.administrator = administrator;
        this.manager = manager;
    }

    /**
     * Simply add the administrator to the ClinicianManager
     */
    @Override
    protected void execute() {
        manager.addAdministrator(administrator);
    }

    /**
     * Simply remove the administrator from the ClinicianManager
     */
    @Override
    protected void unExecute() {
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
}
