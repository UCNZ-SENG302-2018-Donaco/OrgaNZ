package seng302.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Donor;
import seng302.HistoryItem;
import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.CreateUserAction;
import seng302.App;
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

@Command(name = "createuser", description = "Creates a user.")
public class CreateUser implements Runnable {

    private DonorManager manager;
    private ActionInvoker invoker;

    public CreateUser() {
        manager = App.getManager();
        invoker = App.getInvoker();
    }

    CreateUser(DonorManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
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

        Donor donor = new Donor(firstName, middleNames, lastName, dateOfBirth, uid);

        Action action = new CreateUserAction(donor, manager);

        invoker.execute(action);

        System.out.println(String.format("New donor %s %s %s created with userID %s", firstName, ofNullable(middleNames).orElse(""), lastName, uid));
        HistoryItem create = new HistoryItem("CREATE", "Donor profile ID: " + uid + " created.");
        JSONConverter.updateHistory(create, "action_history.json");
    }
}
