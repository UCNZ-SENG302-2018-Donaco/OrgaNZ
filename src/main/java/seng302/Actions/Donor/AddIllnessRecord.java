package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.IllnessRecord;

public class AddIllnessRecord extends Action {

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

  @Override
  public String getExecuteText() {
    return String.format("Added record for illness '%s' to the history of donor %d: %s.",
            record.getIllnessName(), donor.getUid(), donor.getFullName());
  }

  @Override
  public String getUnexecuteText() {
    return String.format("Reversed the addition of record for illness '%s' from the history of donor %d: %s.",
            record.getIllnessName(), donor.getUid(), donor.getFullName());
  }

}
