package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.views.client.ModifyIllnessObject;

import org.springframework.beans.BeanUtils;

public class ModifyIllnessRecordByObjectAction extends ClientAction {

    private IllnessRecord oldRecord;
    private IllnessRecord record;
    private ModifyIllnessObject oldIllnessDetails;

    public ModifyIllnessRecordByObjectAction(IllnessRecord oldRecord, ClientManager manager,
            ModifyIllnessObject oldIllnessDetails,
            ModifyIllnessObject newIllnessDetails) {
        super(oldRecord.getClient(), manager);
        this.oldIllnessDetails = oldIllnessDetails;
        this.oldRecord = oldRecord;

        if (newIllnessDetails.getIllnessName() == null) {
            newIllnessDetails.setIllnessName(record.getIllnessName());
        }
        if (newIllnessDetails.getDiagnosisDate() == null) {
            newIllnessDetails.setDiagnosisDate(record.getDiagnosisDate());
        }
        if (newIllnessDetails.isChronic() == null) {
            newIllnessDetails.setIsChronic(record.isChronic());
        }

        record = new IllnessRecord(newIllnessDetails.getIllnessName(),
                newIllnessDetails.getDiagnosisDate(), newIllnessDetails.getCuredDate(), newIllnessDetails.isChronic());
    }

    @Override
    public void execute() {
        super.execute();
        BeanUtils.copyProperties(oldIllnessDetails.getUnmodifiedFields(), record);
        client.addIllnessRecord(record);
        client.deleteIllnessRecord(oldRecord);
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        BeanUtils.copyProperties(oldIllnessDetails.getUnmodifiedFields(), record);
        client.addIllnessRecord(oldRecord);
        client.deleteIllnessRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Modified record for illness '%s' for client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed the modifications of the record for illness '%s' for client %d: %s.",
                record.getIllnessName(), client.getUid(), client.getFullName());
    }
}

