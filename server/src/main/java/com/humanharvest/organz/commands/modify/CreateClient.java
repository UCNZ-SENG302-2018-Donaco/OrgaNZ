package com.humanharvest.organz.commands.modify;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.CreateClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.pico_type_converters.PicoLocalDateConverter;

import com.humanharvest.organz.utilities.type_converters.LocalDateConverter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to create a Client with basic information, including their DOB and full name.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "createclient", description = "Creates a client.")
public class CreateClient implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;

    public CreateClient() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    public CreateClient(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
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

    public void run() {

        if (!force && manager.doesClientExist(firstName, lastName, dateOfBirth)) {
            System.out.println("Duplicate client found, use --force to create anyway");
            return;
        }
        int uid = manager.nextUid();

        Client client = new Client(firstName, middleNames, lastName, dateOfBirth, uid);

        Action action = new CreateClientAction(client, manager);

        System.out.println(invoker.execute(action));
    }
}
