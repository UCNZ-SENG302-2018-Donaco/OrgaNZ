package com.humanharvest.organz.commands.modify;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.clinician.ModifyClinicianAction;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.pico_type_converters.PicoCountryConverter;
import com.humanharvest.organz.utilities.validators.RegionValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command line for modifying attribute of a clinician
 */
@Command(name = "modifyclinician", description = "Modify the attribute of an existing clinician", sortOptions = false)
public class ModifyClinician implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ModifyClinician.class.getName());

    private final ClinicianManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    @Option(names = {"-s", "--staffId"}, description = "Staff id", required = true)
    private int id; // staffId of the clinician

    @Option(names = {"-f", "--firstname"}, description = "First name.")
    private String firstName;

    @Option(names = {"-l", "--lastname"}, description = "Last name.")
    private String lastName;

    @Option(names = {"-m", "--middlenames", "--middlename"}, description = "Middle name(s)")
    private String middleNames;

    @Option(names = {"-a", "--address"}, description = "Work Address.")
    private String workAddress;

    @Option(names = {"-r", "--region"}, description = "Region.")
    private String region;

    @Option(names = {"-c", "--country"}, description = "Country.", converter = PicoCountryConverter.class)
    private Country country;

    @Option(names = {"-p", "--password"}, description = "Clinician Password.")
    private String password;

    public ModifyClinician(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClinicianManager();
    }

    public ModifyClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }

    @Override
    public void run() {
        Optional<Clinician> clinician = manager.getClinicianByStaffId(id);

        if (!clinician.isPresent()) {
            outputStream.println("No clinician exists with that staff ID");
            return;
        }

        if (!RegionValidator.isValid(country == null ? clinician.get().getCountry() : country, region)) {
            outputStream.printf("%s is not a valid NZ region%n", region);
            return;
        }

        ModifyClinicianAction action = new ModifyClinicianAction(clinician.get(), manager);

        Map<String, Object[]> updates = new HashMap<>();
        updates.put("setFirstName", new String[]{clinician.get().getFirstName(), firstName});
        updates.put("setMiddleName", new String[]{clinician.get().getMiddleName(), middleNames});
        updates.put("setLastName", new String[]{clinician.get().getLastName(), lastName});
        updates.put("setWorkAddress", new String[]{clinician.get().getWorkAddress(), workAddress});
        updates.put("setRegion", new String[]{clinician.get().getRegion(), region});
        updates.put("setCountry", new Country[]{clinician.get().getCountry(), country});
        updates.put("setPassword", new String[]{clinician.get().getPassword(), password});

        for (Entry<String, Object[]> entry : updates.entrySet()) {
            if (entry.getValue()[1] == null) {
                continue;
            }
            try {
                action.addChange(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }

        outputStream.println(invoker.execute(action));

    }
}
