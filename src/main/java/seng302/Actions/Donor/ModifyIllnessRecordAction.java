package seng302.Actions.Donor;

import seng302.Actions.Action;
import java.time.LocalDate;

import java.util.Objects;

import seng302.IllnessRecord;

public class ModifyIllnessRecordAction implements Action{
  private IllnessRecord record;
  private LocalDate oldStarted;
  private LocalDate oldStopped;
  private LocalDate newStarted;
  private LocalDate newStopped;

  public ModifyIllnessRecordAction(IllnessRecord record){
    this.record = record;
    this.oldStarted = record.getDiagnosisDate();
    this.oldStopped = record.getCuredDate();
    this.newStarted = oldStarted;
    this.newStopped = oldStopped;
  }

  public void changeStarted(LocalDate newStarted) {this.newStarted = newStarted;}

  public void changeStopped(LocalDate newStopped) {this.newStopped = newStopped;}

  @Override
  public void execute(){
    if (Objects.equals(newStarted, oldStarted) && Objects.equals(newStopped, oldStopped)) {
        throw new IllegalArgumentException("No changes were made to Illness Record");
      }
    if (!Objects.equals(newStarted, oldStarted)) {
      record.setDiagnosisDate(newStarted);
    }

    if(!Objects.equals(newStopped,oldStopped)){
      record.setCuredDate(newStopped);
    }
  }

  @Override
  public void unExecute(){
    if (!Objects.equals(newStarted, oldStarted)) {
      record.setDiagnosisDate(oldStarted);
    }

    if(!Objects.equals(newStopped,oldStopped)){
      record.setCuredDate(oldStopped);
    }
  }

}
