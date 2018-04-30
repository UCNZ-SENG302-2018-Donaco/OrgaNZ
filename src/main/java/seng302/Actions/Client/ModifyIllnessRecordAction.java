package seng302.Actions.Client;

import java.time.LocalDate;
import java.util.Objects;

import seng302.Actions.Action;
import seng302.IllnessRecord;

public class ModifyIllnessRecordAction extends Action{
  private IllnessRecord record;
  private LocalDate oldStarted;
  private LocalDate oldStopped;
  private LocalDate newStarted;
  private LocalDate newStopped;

  private Boolean oldChronic;
  private Boolean newChronic;

  public ModifyIllnessRecordAction(IllnessRecord record){
    this.record = record;
    oldStarted = record.getDiagnosisDate();
    oldStopped = record.getCuredDate();
    oldChronic = record.getChronic();
    newStarted = oldStarted;
    newStopped = oldStopped;
    newChronic = oldChronic;
  }

  public void changeStarted(LocalDate newStarted) {this.newStarted = newStarted;}

  public void changeStopped(LocalDate newStopped) {this.newStopped = newStopped;}

  public void changeChronicStatus(Boolean newChronic) {this.newChronic = newChronic;}

  @Override
  public void execute(){
    if (!Objects.equals(newStarted, oldStarted)) {
      record.setDiagnosisDate(newStarted);
    }

    if(!Objects.equals(newStopped, oldStopped)){
      record.setCuredDate(newStopped);
    }

    if(!Objects.equals(newChronic, oldChronic)){
        record.setChronic(newChronic);
    }
  }

  @Override
  public void unExecute(){
    if (!Objects.equals(newStarted, oldStarted)) {
      record.setDiagnosisDate(oldStarted);
    }

    if(!Objects.equals(newStopped, oldStopped)){
      record.setCuredDate(oldStopped);
    }

    if(!Objects.equals(newChronic, oldChronic)){
      record.setChronic(oldChronic);
    }
  }

  @Override
  public String getExecuteText() {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("Changed illness record for '%s':", record.getIllnessName()));

    if (!Objects.equals(newStarted, oldStarted)) {
      builder.append(String.format("\nStarted date changed from %s to %s", oldStarted, newStarted));
    }
    if (!Objects.equals(newStopped, oldStopped)) {
      builder.append(String.format("\nStopped date changed from %s to %s", oldStarted, newStarted));
    }
    if(!Objects.equals(newChronic, oldChronic)){
      builder.append(String.format("\nChronic status changed from %s to %s", oldStarted, newStarted));
    }

    return builder.toString();
  }

  @Override
  public String getUnexecuteText() {
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("Reversed these changes to illness record for '%s':", record.getIllnessName()));

    if (!Objects.equals(newStarted, oldStarted)) {
      builder.append(String.format("\nStarted date changed from %s to %s", oldStarted, newStarted));
    }
    if (!Objects.equals(newStopped, oldStopped)) {
      builder.append(String.format("\nStopped date changed from %s to %s", oldStarted, newStarted));
    }
    if(!Objects.equals(newChronic, oldChronic)){
      builder.append(String.format("\nChronic status changed from %s to %s", oldStarted, newStarted));
    }

    return builder.toString();
  }
}
