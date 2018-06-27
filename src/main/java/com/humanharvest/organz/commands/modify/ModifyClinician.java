package com.humanharvest.organz.commands.modify;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.clinician.ModifyClinicianAction;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.type_converters.RegionConverter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

;

/**
 * Command line for modifying attribute of a clinician
 */
@Command(name = "modifyclinician", description = "Modify the attribute of an existing clinician", sortOptions = false)
public class ModifyClinician implements Runnable {

    private ClinicianManager manager;
    private ActionInvoker invoker;

    public ModifyClinician() {
        manager = State.getClinicianManager();
        invoker = State.getInvoker();
    }

    public ModifyClinician(ClinicianManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

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

    @Option(names = {"-r", "--region"}, description = "Region.", converter = RegionConverter.class)
    private Region region;

    @Option(names = {"-p", "--password"}, description = "Clinician Password.")
    private String password;

    @Override
    public void run() {
        Clinician clinician = manager.getClinicianByStaffId(id);

        if (clinician == null) {
            System.out.println("No clinician exists with that staff ID");
            return;
        }

        ModifyClinicianAction action = new ModifyClinicianAction(clinician,manager);

        Map<String, Object[]> updates = new HashMap<>();
        updates.put("setFirstName", new String[]{clinician.getFirstName(), firstName});
        updates.put("setMiddleName", new String[]{clinician.getMiddleName(), middleNames});
        updates.put("setLastName", new String[]{clinician.getLastName(), lastName});
        updates.put("setWorkAddress", new String[]{clinician.getWorkAddress(), workAddress});
        updates.put("setRegion", new Region[]{clinician.getRegion(), region});
        updates.put("setPassword", new String[]{clinician.getPassword(), password});

        for (Entry<String, Object[]> entry : updates.entrySet()) {
            if (entry.getValue()[1] == null) {
                continue;
            }
            try {
                action.addChange(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        System.out.println(invoker.execute(action));

    }
}
