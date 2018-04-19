package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.IllnessRecord;

public class AddIllnessRecord implements Action {

  private Donor donor;
  private IllnessRecord record;

  public AddIllnessRecord(Donor donor, IllnessRecord record){
    this.donor = donor;
    this.record = record;

  }

  @Override
  public void execute(){
    donor.addIllnessRecord(record);

  }

  @Override
  public void unExecute(){
    donor.deleteIllnessRecord(record);
  }

}
