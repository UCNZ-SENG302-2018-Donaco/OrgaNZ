package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.clinician.CreateClinicianAction;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.pico_type_converters.PicoCountryConverter;
import com.humanharvest.organz.utilities.validators.RegionValidator;

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
    private final PrintStream outputStream;

    @Option(names = {"-s", "--staffId"}, description = "Staff id", required = true)
    private int staffId; // staffId of the clinician

    @Option(names = {"-f", "--firstname"}, description = "First name.", required = true)
    private String firstName;

    @Option(names = {"-l", "--lastname"}, description = "Last name.", required = true)
    private String lastName;

    @Option(names = {"-p", "--password"}, description = "Clinician password", required = true)
    private String password;

    @Option(names = {"-m", "--middlenames", "--middlename"}, description = "Middle name(s)")
    private String middleNames;

    @Option(names = {"-a", "--address"}, description = "Work Address.")
    private String workAddress;

    @Option(names = {"-r", "--region"}, description = "Region")
    private String region;

    @Option(names = {"-c", "--country"}, description = "Country", converter = PicoCountryConverter.class)
    private Country country;

    public CreateClinician(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClinicianManager();
    }

    public CreateClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }

    @Override
    public void run() {

        if (manager.doesStaffIdExist(staffId)) {
            // staff ID is taken
            outputStream.println("Staff ID " + staffId + " is already taken");
            return;
        }

        if (!RegionValidator.isValid(country, region)) {
            outputStream.printf("%s is not a valid NZ region%n", region);
            return;
        }

        Clinician clinician = new Clinician(
                firstName,
                middleNames,
                lastName,
                workAddress,
                region,
                country,
                staffId,
                password);

        Action action = new CreateClinicianAction(clinician, manager);

        outputStream.println(invoker.execute(action));

        LOGGER.log(Level.INFO, action.getExecuteText());
    }
}
