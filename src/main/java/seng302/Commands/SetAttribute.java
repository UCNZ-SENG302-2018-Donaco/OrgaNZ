package seng302.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Action;
import seng302.App;
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

    public SetAttribute() {
        manager = App.getManager();
    }

    SetAttribute(DonorManager manager) {
        this.manager = manager;
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
        if (firstName != null) {
            donor.setFirstName(firstName);
        }
        if (middleName != null) {
            donor.setMiddleName(middleName);
        }
        if (lastName != null) {
            donor.setLastName(lastName);
        }
        if (address != null) {
            donor.setCurrentAddress(address);
        }
        if (region != null) {
            donor.setRegion(region);
        }
        if (gender != null) {
            donor.setGender(gender);
        }
        if (bloodType != null) {
            donor.setBloodType(bloodType);
        }
        if (height != 0) {
            donor.setHeight(height);
        }
        if (weight != 0) {
            donor.setWeight(weight);
        }
        if (dateOfBirth != null) {
            donor.setDateOfBirth(dateOfBirth);
        }
        if (dateOfDeath != null) {
            donor.setDateOfDeath(dateOfDeath);
        }
        manager.updateDonor(donor);
        Action setAttribute = new Action("ATTRIBUTE UPDATE", "DETAILS were updated for user " + uid);
        JSONConverter.updateActionHistory(setAttribute, "action_history.json");
    }
}
