package com.humanharvest.organz.commands.modify;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.clinician.DeleteClinicianAction;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.PrintStream;
import java.util.Optional;

/**
 * Command line to modify attributes of clinicians, using their staff id as a reference key
 */
@Command(name = "deleteclinician", description = "Deletes a clinician.")
public class DeleteClinician implements Runnable {

    private final ClinicianManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    @Option(names = {"-s", "--staffId"}, description = "Staff id", required = true)
    private int id; // staffId of the clinician

    @Option(names = "-y", description = "Confirms you would like to execute the removal")
    private boolean yes;

    public DeleteClinician(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClinicianManager();
    }

    public DeleteClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }

    @Override
    public void run() {
        Optional<Clinician> clinician = manager.getClinicianByStaffId(id);

        if (!clinician.isPresent()) {
            outputStream.println("No clinician exists with that user ID");
        } else if (clinician.get().getStaffId() == manager.getDefaultClinician().getStaffId()) {
            outputStream.println("Default clinician cannot be deleted");
        } else if (!yes) {
            outputStream.println(
                    String.format("Removing clinician: %s, with staff id: %s,\nto proceed please rerun the command "
                                    + "with the -y flag",
                            clinician.get().getFullName(),
                            clinician.get().getStaffId()));
        } else {
            Action action = new DeleteClinicianAction(clinician.get(), manager);

            outputStream.println(invoker.execute(action));
            outputStream.println("This removal will only be permanent once the 'save' command is used");
        }
    }
}
