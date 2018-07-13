package com.humanharvest.organz.actions.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Views.Client.ModifyClientObject;
import com.humanharvest.organz.Views.Client.ModifyIllnessObject;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;
import org.springframework.beans.BeanUtils;

public class ModifyIllnessRecordByObjectAction extends Action {

  private Client client;
  private ClientManager manager;
  private ModifyIllnessObject oldIllnessDetails;
  private ModifyIllnessObject newIllnessDetails;

  public ModifyIllnessRecordByObjectAction(Client client, ClientManager manager, ModifyIllnessObject oldIllnessDetails,
      ModifyIllnessObject newIllnessDetails) {
    this.client = client;
    this.manager = manager;
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

