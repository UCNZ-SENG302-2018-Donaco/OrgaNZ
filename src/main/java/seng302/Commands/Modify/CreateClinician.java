package seng302.Commands.Modify;

import java.util.logging.Level;
import java.util.logging.Logger;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Clinician.CreateClinicianAction;
import seng302.Clinician;
import seng302.HistoryItem;
import seng302.State.ClinicianManager;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;

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

    @Option(names = {"-r", "--region"}, description = "Region.")
    private String region;

    @Option(names = {"-p", "--password"}, description = "Clinician Password.")
    private String password;

    public void run() {

        if (manager.collisionExists(staffId)) {
            // staff ID is taken
            System.out.println("Staff ID " + staffId + " is already taken");
            return;
        }

        Region realRegion = null;
        if (region != null) {
            try {
                realRegion = Region.fromString(region);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Unknown region: " + region, e);
                return;
            }
        }

        Clinician clinician = new Clinician(firstName, middleNames, lastName, workAddress, realRegion, staffId,
                password);

        Action action = new CreateClinicianAction(clinician, manager);

        invoker.execute(action);

        LOGGER.log(Level.INFO, action.getExecuteText());
        HistoryItem create = new HistoryItem("CREATE_CLINICIAN", "Clinician staff ID: " + staffId + " created.");
        JSONConverter.updateHistory(create, "action_history.json");
    }
}
