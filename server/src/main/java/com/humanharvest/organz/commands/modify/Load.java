package com.humanharvest.organz.commands.modify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.JSONConverter;
import picocli.CommandLine.Command;

/**
 * Command line to load the information of all the clients from a JSON file,
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 05/03/2018
 */

@Command(name = "load", description = "Load clients from file", sortOptions = false)
public class Load implements Runnable {

    private ClientManager manager;

    public Load() {
        manager = State.getClientManager();
    }

    public Load(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            JSONConverter.loadFromFile(new File("savefile.json"));
            System.out.println(String.format("Loaded %s clients from file", manager.getClients().size()));
        } catch (FileNotFoundException e) {
            System.out.println("No save file found");
        } catch (IOException e) {
            System.out.println("Could not load from file");
        }
    }
}