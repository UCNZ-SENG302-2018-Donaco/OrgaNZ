package seng302.Commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;

@Command(name = "DonorCLI", description = "DonorCLI is a command based management tool for the team-21 donor registration system.", subcommands = {CreateUser.class, SetAttribute.class})
public class BaseCommand implements Runnable {

    public void run() {
        System.out.println("Invalid command");
    }
}
