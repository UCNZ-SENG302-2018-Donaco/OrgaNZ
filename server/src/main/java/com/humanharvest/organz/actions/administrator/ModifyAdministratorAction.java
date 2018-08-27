package com.humanharvest.organz.actions.administrator;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.actions.ModifyObjectByMethodAction;

/**
 * A reversible administrator modification Action
 */
public class ModifyAdministratorAction extends AdministratorAction {

    private ArrayList<ModifyObjectByMethodAction> actions = new ArrayList<>();

    /**
     * Create a new Action
     *
     * @param administrator The administrator to be modified
     */
    public ModifyAdministratorAction(Administrator administrator) {
        super(administrator);
    }

    /**
     * Add a modification to the administrator
     *
     * @param field The setter field of the administrator. Must match a valid setter in the Administrator object
     * @param oldValue The object the field initially had. Should be taken from the Administrator equivalent getter
     * @param newValue The object the field should be update to. Must match the setters Object type
     * @throws NoSuchMethodException Thrown if the Administrator does not have the specified setter
     * @throws NoSuchFieldException Thrown if the Administrator specified setter does not take the same type
     * as given in one of the values
     */
    public void addChange(String field, Object oldValue, Object newValue)
            throws NoSuchMethodException, NoSuchFieldException {
        if (field.equals("setPassword")) {
            actions.add(new ModifyObjectByMethodAction(administrator, field, oldValue, newValue, true));
        } else {
            actions.add(new ModifyObjectByMethodAction(administrator, field, oldValue, newValue, false));
        }
    }

    @Override
    protected void execute() {
        super.execute();
        for (ModifyObjectByMethodAction action : actions) {
            action.execute();
        }
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        for (ModifyObjectByMethodAction action : actions) {
            action.unExecute();
        }
    }

    @Override
    public String getExecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByMethodAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for administrator %s.%nThese changes were made:\n\n%s",
                administrator.getUsername(), changesText);
    }

    @Override
    public String getUnexecuteText() {
        String changesText = actions.stream()
                .map(ModifyObjectByMethodAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed update for administrator %s.%nThese changes were reversed:%n%n%s",
                administrator.getUsername(), changesText);
    }
}
