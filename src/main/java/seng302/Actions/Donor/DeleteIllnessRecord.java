package seng302.Actions.Donor;

import seng302.Actions.Action;
import seng302.Donor;
import seng302.IllnessRecord;

public class DeleteIllnessRecord extends Action {
  private Donor donor;
  private IllnessRecord record;

  public DeleteIllnessRecord(Donor donor,IllnessRecord record){
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

  @Override
  public String getExecuteText() {
    return String.format("Removed record for illness '%s' from the history of donor %d: %s.",
            record.getIllnessName(), donor.getUid(), donor.getFullName());
  }

  @Override
  public String getUnexecuteText() {
    return String.format("Re-added record for illness '%s' to the history of donor %d: %s.",
            record.getIllnessName(), donor.getUid(), donor.getFullName());
  }
}
