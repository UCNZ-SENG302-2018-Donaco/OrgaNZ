package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.views.client.ModifyProcedureObject;
import org.springframework.beans.BeanUtils;

public class ModifyProcedureRecordByObjectAction extends ClientAction {

    private ModifyProcedureObject oldProcedureDetails;
    private ModifyProcedureObject newProcedureDetails;

    /**
     * Create a new Action
     * @param record The {@link ProcedureRecord} to be modified.
     * @param manager The client manager to use when applying the changes.
     * @param oldProcedureDetails The object containing all the old details of the procedure record.
     * @param newProcedureDetails The object containing all the new details of the procedure record.
     */
    public ModifyProcedureRecordByObjectAction(ProcedureRecord record, ClientManager manager,
            ModifyProcedureObject oldProcedureDetails, ModifyProcedureObject newProcedureDetails) {
        super(record.getClient(), manager);
        this.oldProcedureDetails = oldProcedureDetails;
        this.newProcedureDetails = newProcedureDetails;
    }

    @Override
    protected void execute() {
        BeanUtils.copyProperties(newProcedureDetails, client, newProcedureDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        BeanUtils.copyProperties(oldProcedureDetails, client, oldProcedureDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return "Todo";
    }

    @Override
    public String getUnexecuteText() {
        return "Todo";
    }
}
