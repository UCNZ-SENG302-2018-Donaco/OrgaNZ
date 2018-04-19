package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Person;
import seng302.IllnessRecord;

public class AddIllnessRecord implements Action {

  private Person donor;
  private IllnessRecord record;

  public AddIllnessRecord(Person donor, IllnessRecord record){
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
