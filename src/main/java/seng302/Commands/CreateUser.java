package seng302.Commands;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import seng302.Action;
import seng302.App;
import seng302.Command.Command;
import seng302.Command.CommandInvoker;
import seng302.Command.CreateUserCommand;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.LocalDateConverter;

import java.time.LocalDate;

import static java.util.Optional.ofNullable;

/**
 * Command line to create a Donor with basic information, including their DOB and full name.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@CommandLine.Command(name = "createuser", description = "Creates a user.")
public class CreateUser implements Runnable {

    private DonorManager manager;
    private CommandInvoker invoker;

    public CreateUser() {
        manager = App.getManager();
        invoker = App.getInvoker();
    }

    CreateUser(DonorManager manager) {
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
        if (!force && manager.collisionExists(firstName, lastName, dateOfBirth)) {
            System.out.println("Duplicate user found, use --force to create anyway");
            return;
        }
        int uid = manager.getUid();

        Command command = new CreateUserCommand(firstName, middleNames, lastName, dateOfBirth, uid, manager);

        invoker.execute(command);

        System.out.println(String.format("New donor %s %s %s created with userID %s", firstName, ofNullable(middleNames).orElse(""), lastName, uid));
        Action create = new Action("CREATE", "Donor profile ID: " + uid + " created.");
        JSONConverter.updateActionHistory(create, "action_history.json");
    }
}
