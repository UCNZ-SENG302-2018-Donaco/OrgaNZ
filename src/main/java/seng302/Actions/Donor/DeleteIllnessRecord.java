package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Person;
import seng302.IllnessRecord;

public class DeleteIllnessRecord implements Action {
  private Person donor;
  private IllnessRecord record;

  public DeleteIllnessRecord(Person donor,IllnessRecord record){
    this.donor = donor;
    this.record = record;


  }

  @Override
  public void execute(){
    donor.deleteIllnessRecord(record);

  }
  @Override
  public void unExecute(){
    donor.addIllnessRecord(record);
  }


}
