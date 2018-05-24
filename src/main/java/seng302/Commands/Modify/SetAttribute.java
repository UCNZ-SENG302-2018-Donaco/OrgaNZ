package seng302.Commands.Modify;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ModifyClientAction;
import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
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
 * Command line to set attributes of a Client, by using their ID as a reference key.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "setattribute", description = "Set the attributes of an existing client.", sortOptions = false)
public class SetAttribute implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;

    public SetAttribute() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    public SetAttribute(ClientManager manager, ActionInvoker invoker) {
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
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }

        ModifyClientAction action = new ModifyClientAction(client, manager);

        Map<String, Object[]> states = new HashMap<>();
        states.put("setFirstName", new String[]{client.getFirstName(), firstName});
        states.put("setMiddleName", new String[]{client.getMiddleName(), middleName});
        states.put("setLastName", new String[]{client.getLastName(), lastName});
        states.put("setCurrentAddress", new String[]{client.getCurrentAddress(), address});
        states.put("setRegion", new Region[]{client.getRegion(), region});
        states.put("setGender", new Gender[]{client.getGender(), gender});
        states.put("setBloodType", new BloodType[]{client.getBloodType(), bloodType});
        states.put("setHeight", new Double[]{client.getHeight(), height});
        states.put("setWeight", new Double[]{client.getWeight(), weight});
        states.put("setDateOfBirth", new LocalDate[]{client.getDateOfBirth(), dateOfBirth});
        states.put("setDateOfDeath", new LocalDate[]{client.getDateOfDeath(), dateOfDeath});

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

        System.out.println(invoker.execute(action));

        HistoryItem setAttribute = new HistoryItem("ATTRIBUTE UPDATE", "DETAILS were updated for client " + uid);
        JSONConverter.updateHistory(setAttribute, "action_history.json");
    }
}
