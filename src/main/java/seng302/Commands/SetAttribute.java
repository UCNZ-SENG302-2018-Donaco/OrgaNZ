package seng302.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Actions.ActionInvoker;
import seng302.Actions.ModifyDonorAction;
import seng302.HistoryItem;
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.*;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;

/**
 * Command line to set attributes of a Donor, by using their ID as a reference key.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@Command(name = "setattribute", description = "Set the attributes of an existing user.", sortOptions = false)
public class SetAttribute implements Runnable {

    private DonorManager manager;
    private ActionInvoker invoker;

    public SetAttribute() {
        manager = State.getDonorManager();
        invoker = State.getInvoker();
    }

    SetAttribute(DonorManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Option(names = "--firstname", description = "First name")
    private String firstName;

    @Option(names = "--middlename", description = "Middle name(s)")
    private String middleName;

    @Option(names = "--lastname", description = "Last name")
    private String lastName;

    @Option(names = "--currentaddress", description = "Current address")
    private String address;

    @Option(names = "--region", description = "Region", converter = RegionConverter.class)
    private Region region;

    @Option(names = "--gender", description = "Gender", converter = GenderConverter.class)
    private Gender gender;

    @Option(names = "--bloodtype", description = "Blood type", converter = BloodTypeConverter.class)
    private BloodType bloodType;

    @Option(names = "--height", description = "Height (cm)")
    private Double height;

    @Option(names = "--weight", description = "Weight (kg)")
    private Double weight;

    @Option(names = "--dateofbirth", description = "Date of birth (dd/mm/yyyy)", converter = LocalDateConverter.class)
    private LocalDate dateOfBirth;

    @Option(names = "--dateofdeath", description = "Date of death (dd/mm/yyyy)", converter = LocalDateConverter.class)
    private LocalDate dateOfDeath;


    @Override
    public void run() {
        Donor donor = manager.getDonorByID(uid);
        if (donor == null) {
            System.out.println("No donor exists with that user ID");
            return;
        }

        ModifyDonorAction action = new ModifyDonorAction(donor);

        Map<String, Object[]> states = new HashMap<>();
        states.put("setFirstName", new String[]{donor.getFirstName(), firstName});
        states.put("setMiddleName", new String[]{donor.getMiddleName(), middleName});
        states.put("setLastName", new String[]{donor.getLastName(), lastName});
        states.put("setCurrentAddress", new String[]{donor.getCurrentAddress(), address});
        states.put("setRegion", new Region[]{donor.getRegion(), region});
        states.put("setGender", new Gender[]{donor.getGender(), gender});
        states.put("setBloodType", new BloodType[]{donor.getBloodType(), bloodType});
        states.put("setHeight", new Double[]{donor.getHeight(), height});
        states.put("setWeight", new Double[]{donor.getWeight(), weight});
        states.put("setDateOfBirth", new LocalDate[]{donor.getDateOfBirth(), dateOfBirth});
        states.put("setDateOfDeath", new LocalDate[]{donor.getDateOfDeath(), dateOfDeath});

        for (Entry<String, Object[]> entry : states.entrySet()) {
            if (entry.getValue()[1] == null) {
                continue;
            }
            try {
                action.addChange(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        invoker.execute(action);
        HistoryItem setAttribute = new HistoryItem("ATTRIBUTE UPDATE", "DETAILS were updated for user " + uid);
        JSONConverter.updateHistory(setAttribute, "action_history.json");
    }
}
