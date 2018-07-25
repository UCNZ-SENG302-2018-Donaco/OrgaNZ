package com.humanharvest.organz.commands.view;

import java.io.PrintStream;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print all of the information of a single client.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 06/03/2018
 */

@Command(name = "getchanges", description = "Print a single clients update history.", sortOptions = false)
public class GetChanges implements Runnable {

    private final ClientManager manager;
    private final PrintStream outputStream;

    public GetChanges() {
        manager = State.getClientManager();
        outputStream = System.out;
    }

    public GetChanges(PrintStream outputStream) {
        manager = State.getClientManager();
        this.outputStream = outputStream;
    }

    GetChanges(ClientManager manager) {
        this.manager = manager;
        outputStream = System.out;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Override
    public void run() {
        Optional<Client> client = manager.getClientByID(uid);
        if (client.isPresent()) {
            outputStream.println(client.get().getUpdatesString());
        } else {
            outputStream.println("No client exists with that user ID");
        }
    }
}
