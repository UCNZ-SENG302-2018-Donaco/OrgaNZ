package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.views.client.ModifyIllnessObject;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;
import org.springframework.beans.BeanUtils;

public class ModifyIllnessRecordByObjectAction extends ClientAction {
  private IllnessRecord oldRecord;
  private IllnessRecord record;
  private ModifyIllnessObject oldIllnessDetails;
  private ModifyIllnessObject newIllnessDetails;

  public ModifyIllnessRecordByObjectAction(IllnessRecord oldRecord, ClientManager manager, ModifyIllnessObject oldIllnessDetails,
      ModifyIllnessObject newIllnessDetails) {
    super(oldRecord.getClient(), manager);
    this.oldIllnessDetails = oldIllnessDetails;
    this.newIllnessDetails = newIllnessDetails;
    this.oldRecord = oldRecord;
  }

  @Override
  public void execute() {
    IllnessRecord record = new IllnessRecord(newIllnessDetails.getIllnessName(),
        newIllnessDetails.getDiagnosisDate(),newIllnessDetails.getCuredDate(),newIllnessDetails.isChronic());
    BeanUtils.copyProperties(oldIllnessDetails.getUnmodifiedFields(),record);
    client.addIllnessRecord(record);
    client.deleteIllnessRecord(oldRecord);
  }

  @Override
  protected void unExecute() {
    IllnessRecord record = new IllnessRecord(newIllnessDetails.getIllnessName(),
        newIllnessDetails.getDiagnosisDate(),newIllnessDetails.getCuredDate(),newIllnessDetails.isChronic());
    BeanUtils.copyProperties(oldIllnessDetails.getUnmodifiedFields(),record);
    client.addIllnessRecord(oldRecord);
    client.deleteIllnessRecord(record);
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

