package com.humanharvest.organz.actions.client.illness;

import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.actions.client.ClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.views.client.ModifyIllnessObject;

import org.springframework.beans.BeanUtils;

public class ModifyIllnessRecordByObjectAction extends ClientAction {

    private final IllnessRecord illnessRecord;
    private final ModifyIllnessObject oldIllnessDetails;
    private final ModifyIllnessObject newIllnessDetails;

    public ModifyIllnessRecordByObjectAction(IllnessRecord illnessRecord, ClientManager manager,
            ModifyIllnessObject oldIllnessDetails,
            ModifyIllnessObject newIllnessDetails) {
        super(illnessRecord.getClient(), manager);

        this.illnessRecord = illnessRecord;
        this.oldIllnessDetails = oldIllnessDetails;
        this.newIllnessDetails = newIllnessDetails;
    }

    @Override
    protected void execute() {
        super.execute();
        BeanUtils.copyProperties(newIllnessDetails, illnessRecord, newIllnessDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        BeanUtils.copyProperties(oldIllnessDetails, illnessRecord, oldIllnessDetails.getUnmodifiedFields());
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Modified record for illness '%s' for client %d: %s.",
                illnessRecord.getIllnessName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the modifications of the record for illness '%s' for client %d: %s.",
                illnessRecord.getIllnessName(), client.getUid(), client.getFullName());
    }
}

