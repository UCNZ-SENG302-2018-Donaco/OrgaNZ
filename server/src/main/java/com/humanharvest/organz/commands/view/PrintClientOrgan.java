package com.humanharvest.organz.commands.view;

import java.io.PrintStream;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command line to print the donation information of a client.
 */

@Command(name = "printclientorgan", description = "Print a single client with their organ information.", sortOptions =
        false)
public class PrintClientOrgan implements Runnable {

    private final ClientManager manager;
    private final PrintStream outputStream;

    public PrintClientOrgan() {
        manager = State.getClientManager();
        outputStream = System.out;
    }

    public PrintClientOrgan(PrintStream outputStream) {
        manager = State.getClientManager();
        this.outputStream = outputStream;
    }

    public PrintClientOrgan(ClientManager manager) {
        this.manager = manager;
        outputStream = System.out;
    }

    @Option(names = {"--id", "-u"}, description = "User ID", required = true)
    private int uid;

    @Option(names = {"-t", "-type"}, description = "Organ donations or requests", required = true)
    private String type;

    @Override
    public void run() {
        // printuserorgan -u=1 -t=requests
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            outputStream.println("No client exists with that user ID");
            return;
        }
        if ("requests".equals(type) || "donations".equals(type)) {
            outputStream.println(client.get().getClientOrganStatusString(type));
        } else {
            outputStream.println("Define if organs to print are donations or requests e.g. 'printuserorgan "
                    + "-uid=1 -t=requests'");
        }
    }
}