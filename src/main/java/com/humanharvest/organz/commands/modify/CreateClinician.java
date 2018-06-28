package com.humanharvest.organz.commands.modify;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.clinician.CreateClinicianAction;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.type_converters.RegionConverter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to create a Clinician with basic information.
 */
@Command(name = "createclinician", description = "Creates a clinician.")
public class CreateClinician implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(CreateClinician.class.getName());

    private final ClinicianManager manager;
    private final ActionInvoker invoker;

    public CreateClinician() {
        this(State.getClinicianManager(), State.getInvoker());
    }

    public CreateClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"-s", "--staffId"}, description = "Staff id", required = true)
    private int staffId; // staffId of the clinician

    @Option(names = {"-f", "--firstname"}, description = "First name.", required = true)
    private String firstName;

    @Option(names = {"-l", "--lastname"}, description = "Last name.", required = true)
    private String lastName;

    @Option(names = {"-m", "--middlenames", "--middlename"}, description = "Middle name(s)")
    private String middleNames;

    @Option(names = {"-a", "--address"}, description = "Work Address.")
    private String workAddress;

    @Option(names = {"-r", "--region"}, description = "Region", converter = RegionConverter.class)
    private Region region;

    @Option(names = {"-p", "--password"}, description = "Clinician Password.")
    private String password;

    public void run() {

        if (manager.collisionExists(staffId)) {
            // staff ID is taken
            System.out.println("Staff ID " + staffId + " is already taken");
            return;
        }

        Clinician clinician = new Clinician(firstName, middleNames, lastName, workAddress, region, staffId,
                password);

        Action action = new CreateClinicianAction(clinician, manager);

        System.out.println(invoker.execute(action));

        LOGGER.log(Level.INFO, action.getExecuteText());
    }
}
