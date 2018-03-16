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
    private int height;

    @Option(names = "--weight", description = "Weight (kg)")
    private int weight;

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

        if (firstName != null) {
            action.addChange("setFirstName", donor.getFirstName(), firstName);
        }
        if (middleName != null) {
            action.addChange("setMiddleName", donor.getMiddleName(), middleName);
        }
        if (lastName != null) {
            action.addChange("setLastName", donor.getLastName(), lastName);
        }
        if (address != null) {
            action.addChange("setCurrentAddress", donor.getCurrentAddress(), address);
        }
        if (region != null) {
            action.addChange("setRegion", donor.getRegion(), region);
        }
        if (gender != null) {
            action.addChange("setGender", donor.getGender(), gender);
        }
        if (bloodType != null) {
            action.addChange("setBloodType", donor.getBloodType(), bloodType);
        }
        if (height != 0) {
            action.addChange("setHeight", donor.getHeight(), height);
        }
        if (weight != 0) {
            action.addChange("setWeight", donor.getWeight(), weight);
        }
        if (dateOfBirth != null) {
            action.addChange("setDateOfBirth", donor.getDateOfBirth(), dateOfBirth);
        }
        if (dateOfDeath != null) {
            action.addChange("setDateOfDeath", donor.getDateOfDeath(), dateOfDeath);
        }
        invoker.execute(action);
        HistoryItem setAttribute = new HistoryItem("ATTRIBUTE UPDATE", "DETAILS were updated for user " + uid);
        JSONConverter.updateHistory(setAttribute, "action_history.json");
    }
}
