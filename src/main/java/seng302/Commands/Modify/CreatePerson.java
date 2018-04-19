package seng302.Commands.Modify;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Person.CreatePersonAction;
import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.TypeConverters.LocalDateConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to create a Person with basic information, including their DOB and full name.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "createuser", description = "Creates a user.")
public class CreatePerson implements Runnable {

    private PersonManager manager;
    private ActionInvoker invoker;

    public CreatePerson() {
        manager = State.getPersonManager();
        invoker = State.getInvoker();
    }

    public CreatePerson(PersonManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"-f", "--firstname"}, description = "First name.", required = true)
    private String firstName;

    @Option(names = {"-l", "--lastname"}, description = "Last name.", required = true)
    private String lastName;

    @Option(names = {"-d",
            "--dob"}, description = "Date of birth.", required = true, converter = LocalDateConverter.class)
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

        Person person = new Person(firstName, middleNames, lastName, dateOfBirth, uid);

        Action action = new CreatePersonAction(person, manager);

        invoker.execute(action);

        System.out.println(String.format("New person %s %s %s created with userID %s", firstName,
                ofNullable(middleNames).orElse(""), lastName, uid));
        HistoryItem create = new HistoryItem("CREATE", "Person profile ID: " + uid + " created.");
        JSONConverter.updateHistory(create, "action_history.json");
    }
}
