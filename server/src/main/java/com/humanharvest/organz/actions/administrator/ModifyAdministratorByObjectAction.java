package com.humanharvest.organz.actions.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.AdministratorManager;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;
import org.springframework.beans.BeanUtils;

public class ModifyAdministratorByObjectAction extends Action {

    private final Administrator administrator;
    private final AdministratorManager manager;
    private final ModifyAdministratorObject oldDetails;
    private final ModifyAdministratorObject newDetails;

    public ModifyAdministratorByObjectAction(Administrator administrator,
            AdministratorManager manager,
            ModifyAdministratorObject oldDetails,
            ModifyAdministratorObject newDetails) {
        this.administrator = administrator;
        this.manager = manager;
        this.oldDetails = oldDetails;
        this.newDetails = newDetails;
    }

    @Override
    protected void execute() {
        BeanUtils.copyProperties(newDetails, administrator, newDetails.getUnmodifiedFields());
        manager.applyChangesTo(administrator);
    }

    @Override
    protected void unExecute() {
        BeanUtils.copyProperties(oldDetails, administrator, oldDetails.getUnmodifiedFields());
        manager.applyChangesTo(administrator);
    }

    @Override
    public String getExecuteText() {
        return "Todo";
    }

    @Override
    public String getUnexecuteText() {
        return "Todo";
    }

    @Override
    public Object getModifiedObject() {
        return administrator;
    }
}
