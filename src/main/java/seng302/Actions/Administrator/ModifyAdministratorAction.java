package seng302.Actions.Administrator;

import java.util.ArrayList;
import java.util.stream.Collectors;

import seng302.Actions.Action;
import seng302.Actions.ModifyObjectByFieldAction;
import seng302.Administrator;

/**
 * A reversible administrator modification Action
 */
public class ModifyAdministratorAction extends Action {

    private ArrayList<ModifyObjectByFieldAction> actions = new ArrayList<>();
    private Administrator administrator;

    /**
     * Create a new Action
     * @param administrator The administrator to be modified
     */
    public ModifyAdministratorAction(Administrator administrator) {
        this.administrator = administrator;
    }

    /**
     * Add a modification to the administrator
     * @param field The setter field of the administrator. Must match a valid setter in the Administrator object
     * @param oldValue The object the field initially had. Should be taken from the Administrator equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Administrator does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Administrator specified setter does not take the same type as given in
     * one of the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchMethodException, NoSuchFieldException {
        if (field.equals("setPassword")) {
            actions.add(new ModifyObjectByFieldAction(administrator, field, oldValue, newValue, true));
        } else {
            actions.add(new ModifyObjectByFieldAction(administrator, field, oldValue, newValue, false));
        }
    }

    @Override
    protected void execute() {
        for (ModifyObjectByFieldAction action : actions) {
            action.execute();
        }
    }

    @Override
    protected void unExecute() {
        for (ModifyObjectByFieldAction action : actions) {
            action.unExecute();
        }
    }

    @Override
    public String getExecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByFieldAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for administrator %s.\nThese changes were made:\n\n%s",
                administrator.getUsername(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByFieldAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for administrator %s.\nThese changes were reversed:\n\n%s",
                administrator.getUsername(), changesText);
    }
}
