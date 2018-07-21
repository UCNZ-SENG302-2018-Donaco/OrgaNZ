package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.views.client.ModifyIllnessObject;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;
import org.springframework.beans.BeanUtils;

public class ModifyIllnessRecordByObjectAction extends ClientAction {

  private ModifyIllnessObject oldIllnessDetails;
  private ModifyIllnessObject newIllnessDetails;

  public ModifyIllnessRecordByObjectAction(Client client, ClientManager manager, ModifyIllnessObject oldIllnessDetails,
      ModifyIllnessObject newIllnessDetails) {
    super(client, manager);
    this.oldIllnessDetails = oldIllnessDetails;
    this.newIllnessDetails = newIllnessDetails;
  }

  @Override
  protected void execute() {
    BeanUtils.copyProperties(newIllnessDetails, client, newIllnessDetails.getUnmodifiedFields());
    manager.applyChangesTo(client);
  }

  @Override
  protected void unExecute() {
    BeanUtils.copyProperties(oldIllnessDetails, client, oldIllnessDetails.getUnmodifiedFields());
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

