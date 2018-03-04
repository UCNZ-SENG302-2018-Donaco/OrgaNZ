package seng302.Commands;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import seng302.App;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.*;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

@Command(name = "createuser", description = "Creates a user.")
public class CreateUser implements Runnable {

    private DonorManager manager;

    public CreateUser() {
        manager = App.getManager();
    }

    public CreateUser(DonorManager manager) {
        this.manager = manager;
    }

    @Option(names = {"-f", "--firstname"}, description = "First name.", required = true)
    private String firstName;

    @Option(names = {"-l", "--lastname"}, description = "Last name.", required = true)
    private String lastName;

    @Option(names = {"-d", "--dob"}, description = "Date of birth.", required = true, converter = LocalDateConverter.class)
    private LocalDate dateOfBirth;

    @Option(names = {"-m", "--middlenames", "--middlename"}, description = "Middle name(s)")
    private String middleNames;

    @Option(names = "--force", description = "Force even if a duplicate user is found")
    private boolean force;

    public void run() {
        if (manager.collisionExists(firstName, lastName, dateOfBirth) && !force) {
            System.out.println("Duplicate user found, use --force to create anyway");
            return;
        }
        int uid = manager.getUid();

        Donor donor = new Donor(firstName, middleNames, lastName, dateOfBirth, uid);
        manager.addDonor(donor);
        System.out.println(String.format("New donor %s %s %s created with userID %s", firstName, ofNullable(middleNames).orElse(""), lastName, uid));
    }
}
