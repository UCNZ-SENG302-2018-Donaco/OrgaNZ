package seng302.Commands.Modify;
;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Clinician.ModifyClinicianAction;
import seng302.Clinician;
import seng302.HistoryItem;
import seng302.State.ClinicianManager;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.TypeConverters.RegionConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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

        HistoryItem setAttribute = new HistoryItem("ATTRIBUTE UPDATE", "DETAILS were updated for clinician " + id);
        JSONConverter.updateHistory(setAttribute, "action_history.json");

    }
}
