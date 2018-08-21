package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.CreateClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.pico_type_converters.PicoLocalDateConverter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to create a Client with basic information, including their DOB and full name.
 */

@Command(name = "createclient", description = "Creates a client.")
public class CreateClient implements Runnable {

    private final ClientManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    public CreateClient(PrintStream outputStream, ActionInvoker invoker) {
        this.outputStream = outputStream;
        this.invoker = invoker;
        manager = State.getClientManager();
    }

    public CreateClient(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }

    @Option(names = {"-f", "--firstname"}, description = "First name.", required = true)
    private String firstName;

    @Option(names = {"-l", "--lastname"}, description = "Last name.", required = true)
    private String lastName;

    @Option(names = {"-d",
            "--dob"}, description = "Date of birth.", required = true, converter = PicoLocalDateConverter.class)
    private LocalDate dateOfBirth;

    @Option(names = {"-m", "--middlenames", "--middlename"}, description = "Middle name(s)")
    private String middleNames;

    @Option(names = "--force", description = "Force even if a duplicate client is found")
    private boolean force;

    @Override
    public void run() {

        if (!force && manager.doesClientExist(firstName, lastName, dateOfBirth)) {
            outputStream.println("Duplicate client found, use --force to create anyway");
            return;
        }

        Client client = new Client(firstName, middleNames, lastName, dateOfBirth, null);

        Action action = new CreateClientAction(client, manager);

        outputStream.println(invoker.execute(action));
    }
}
