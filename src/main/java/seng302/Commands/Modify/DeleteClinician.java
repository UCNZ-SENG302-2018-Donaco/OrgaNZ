package seng302.Commands.Modify;

import java.util.Scanner;

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

@Command(name = "deleteclinician", description = "Deletes a clinician.")
public class DeleteClinician implements Runnable{

    private ClinicianManager manager;
    private ActionInvoker invoker;

    @Option(names = {"-s", "--staffId"}, description = "Staff id", required = true)
    private int id;

    public DeleteClinician() {
        manager = State.getClinicianManager();
        invoker = State.getInvoker();
    }

    public DeleteClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    public void run() {
        Clinician clinician = manager.getClinicianByStaffId(id);

        if (clinician == null) {
            System.out.println("No clinician exists with that user ID");
        } else {
            System.out.println(
                    String.format("Removing clinician: %s, with staff id: %s, would you like to proceed? (y/n)",
                            clinician.getFullName(), clinician.getStaffId())
            );
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();

            if (response.equals("y")) {
                Action action = new DeleteClinicianAction(clinician, manager);
                invoker.execute(action);

                System.out.println("Clinician" + id + "removed. This removal will only be permanent once the 'save' "
                        + "command is used");
                HistoryItem deleteClinician = new HistoryItem("DELETE", "Clinician" + id + "deleted");
                JSONConverter.updateHistory(deleteClinician, "action_history.json");
            }else {
                System.out.println("Clinician not removed");
            }
        }
    }
}
