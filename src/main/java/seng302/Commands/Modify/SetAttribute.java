package seng302.Commands.Modify;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Person.ModifyPersonAction;
import seng302.Person;
import seng302.HistoryItem;
import seng302.State.PersonManager;
import seng302.State.State;
import seng302.Utilities.Enums.BloodType;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.TypeConverters.BloodTypeConverter;
import seng302.Utilities.TypeConverters.GenderConverter;
import seng302.Utilities.TypeConverters.LocalDateConverter;
import seng302.Utilities.TypeConverters.RegionConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to set attributes of a Person, by using their ID as a reference key.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "setattribute", description = "Set the attributes of an existing user.", sortOptions = false)
public class SetAttribute implements Runnable {

    private PersonManager manager;
    private ActionInvoker invoker;

    public SetAttribute() {
        manager = State.getPersonManager();
        invoker = State.getInvoker();
    }

    public SetAttribute(PersonManager manager, ActionInvoker invoker) {
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
        Person person = manager.getPersonByID(uid);
        if (person == null) {
            System.out.println("No person exists with that user ID");
            return;
        }

        ModifyPersonAction action = new ModifyPersonAction(person);

        Map<String, Object[]> states = new HashMap<>();
        states.put("setFirstName", new String[]{person.getFirstName(), firstName});
        states.put("setMiddleName", new String[]{person.getMiddleName(), middleName});
        states.put("setLastName", new String[]{person.getLastName(), lastName});
        states.put("setCurrentAddress", new String[]{person.getCurrentAddress(), address});
        states.put("setRegion", new Region[]{person.getRegion(), region});
        states.put("setGender", new Gender[]{person.getGender(), gender});
        states.put("setBloodType", new BloodType[]{person.getBloodType(), bloodType});
        states.put("setHeight", new Double[]{person.getHeight(), height});
        states.put("setWeight", new Double[]{person.getWeight(), weight});
        states.put("setDateOfBirth", new LocalDate[]{person.getDateOfBirth(), dateOfBirth});
        states.put("setDateOfDeath", new LocalDate[]{person.getDateOfDeath(), dateOfDeath});

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
