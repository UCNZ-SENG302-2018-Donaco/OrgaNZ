package seng302.Commands.Modify;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Clinician.DeleteClinicianAction;
import seng302.Clinician;
import seng302.HistoryItem;
import seng302.State.ClinicianManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to modify attributes of clinicians, using their staff id as a reference key
 */
@Command(name = "deleteclinician", description = "Deletes a clinician.")
public class DeleteClinician implements Runnable {

    private ClinicianManager manager;
    private ActionInvoker invoker;

    @Option(names = {"-s", "--staffId"}, description = "Staff id", required = true)
    private int id; // staffId of the clinician

    @Option(names = "-y", description = "Confirms you would like to execute the removal")
    private boolean yes;

    public DeleteClinician() {
        manager = State.getClinicianManager();
        invoker = State.getInvoker();
    }

    public DeleteClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        Clinician clinician = manager.getClinicianByStaffId(id);

        if (clinician == null) {
            System.out.println("No clinician exists with that user ID");
        } else if (clinician.getStaffId() == manager.getDefaultClinician().getStaffId()) {
            System.out.println("Default clinician cannot be deleted");
        } else if (!yes) {
            System.out.println(
                    String.format("Removing clinician: %s, with staff id: %s,\nto proceed please rerun the command "
                                    + "with the -y flag",
                            clinician.getFullName(),
                            clinician.getStaffId()));
        } else {
            Action action = new DeleteClinicianAction(clinician, manager);
            invoker.execute(action);

            System.out.println("Clinician " + id + " removed. This removal will only be permanent once the 'save' "
                    + "command is used");
            HistoryItem deleteClinician = new HistoryItem("DELETE", "Clinician " + id + " deleted");
            JSONConverter.updateHistory(deleteClinician, "action_history.json");
        }
    }
}
