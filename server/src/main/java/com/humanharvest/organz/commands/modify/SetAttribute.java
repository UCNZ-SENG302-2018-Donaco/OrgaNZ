package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.ModifyClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.pico_type_converters.PicoBloodTypeConverter;
import com.humanharvest.organz.utilities.pico_type_converters.PicoCountryConverter;
import com.humanharvest.organz.utilities.pico_type_converters.PicoGenderConverter;
import com.humanharvest.organz.utilities.pico_type_converters.PicoLocalDateConverter;
import com.humanharvest.organz.utilities.validators.RegionValidator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to set attributes of a Client, by using their ID as a reference key.
 */

@Command(name = "attribute", description = "Set the attributes of an existing client.", sortOptions = false)
public class SetAttribute implements Runnable {

    private final ClientManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    public SetAttribute(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClientManager();
    }

    public SetAttribute(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
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

    @Option(names = "--country", description = "Country", converter = PicoCountryConverter.class)
    private Country country;

    @Option(names = "--gender", description = "Gender", converter = PicoGenderConverter.class)
    private Gender gender;

    @Option(names = "--bloodtype", description = "Blood type", converter = PicoBloodTypeConverter.class)
    private BloodType bloodType;

    @Option(names = "--height", description = "Height (cm)")
    private Double height;

    @Option(names = "--weight", description = "Weight (kg)")
    private Double weight;

    @Option(names = "--dateofbirth", description = "Date of birth (dd/mm/yyyy)",
            converter = PicoLocalDateConverter.class)
    private LocalDate dateOfBirth;

    @Option(names = "--dateofdeath", description = "Date of death (dd/mm/yyyy)",
            converter = PicoLocalDateConverter.class)
    private LocalDate dateOfDeath;


    @Override
    public void run() {
        Optional<Client> possibleClient = manager.getClientByID(uid);
        if (!possibleClient.isPresent()) {
            outputStream.println("No client exists with that user ID");
            return;
        }

        Client client = possibleClient.get();

        if (!RegionValidator.isValid(country == null ? client.getCountry() : country, region)) {
            outputStream.printf("%s is not a valid NZ region%n", region);
            return;
        }

        ModifyClientAction action = new ModifyClientAction(client, manager);

        Map<String, Object[]> states = new HashMap<>();
        states.put("firstName", new String[]{client.getFirstName(), firstName});
        states.put("middleName", new String[]{client.getMiddleName(), middleName});
        states.put("lastName", new String[]{client.getLastName(), lastName});
        states.put("currentAddressgender", new String[]{client.getCurrentAddress(), address});
        states.put("region", new String[]{client.getRegion(), region});
        states.put("country", new Country[]{client.getCountry(), country});
        states.put("gender", new Gender[]{client.getGender(), gender});
        states.put("bloodType", new BloodType[]{client.getBloodType(), bloodType});
        states.put("height", new Double[]{client.getHeight(), height});
        states.put("weight", new Double[]{client.getWeight(), weight});
        states.put("dateOfBirth", new LocalDate[]{client.getDateOfBirth(), dateOfBirth});
        states.put("dateOfDeath", new LocalDate[]{client.getDateOfDeath(), dateOfDeath});

        for (Entry<String, Object[]> entry : states.entrySet()) {
            if (entry.getValue()[1] == null) {
                continue;
            }
            try {
                action.addChange(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
            } catch (NoSuchFieldException e) {
                e.printStackTrace(outputStream);
            }
        }

        outputStream.println(invoker.execute(action));
    }
}
