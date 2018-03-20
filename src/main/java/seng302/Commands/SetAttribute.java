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
        manager = State.getManager();
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

    @Option(names = "--region", description = "Region")
    private String region;

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
        Map<String, Entry<Object, Object>> states = new HashMap<>();
        states.put("setFirstName", new SimpleEntry<>(donor.getFirstName(), firstName));
        states.put("setMiddleName", new SimpleEntry<>(donor.getMiddleName(), middleName));
        states.put("setLastName", new SimpleEntry<>(donor.getLastName(), lastName));
        states.put("setCurrentAddress", new SimpleEntry<>(donor.getCurrentAddress(), address));
        states.put("setRegion", new SimpleEntry<>(donor.getRegion(), region));
        states.put("setGender", new SimpleEntry<>(donor.getGender(), gender));
        states.put("setBloodType", new SimpleEntry<>(donor.getBloodType(), bloodType));
        states.put("setHeight", new SimpleEntry<>(donor.getHeight(), height));
        states.put("setWeight", new SimpleEntry<>(donor.getWeight(), weight));
        states.put("setDateOfBirth", new SimpleEntry<>(donor.getDateOfBirth(), dateOfBirth));
        states.put("setDateOfDeath", new SimpleEntry<>(donor.getDateOfDeath(), dateOfDeath));

        for (Entry<String, Entry<Object, Object>> entry : states.entrySet()) {
            if (entry.getValue().getValue() == null) {
                continue;
            }
            try {
                action.addChange(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
            } catch (NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
        invoker.execute(action);
        HistoryItem setAttribute = new HistoryItem("ATTRIBUTE UPDATE", "DETAILS were updated for user " + uid);
        JSONConverter.updateHistory(setAttribute, "action_history.json");
    }
}
