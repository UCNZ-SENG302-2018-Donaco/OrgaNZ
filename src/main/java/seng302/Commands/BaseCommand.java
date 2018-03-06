package seng302.Commands;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;

/**
 * The main command hub used to access the other commands within the program such as save, help, createuser etc.
 *
 *@author Dylan Carlyle, Jack Steel
 *@version sprint 1.
 *date 05/03/2018
 */

@Command(name = "DonorCLI", description = "DonorCLI is a command based management tool for the team-21 donor registration system.",
        subcommands = {CreateUser.class,
                SetAttribute.class,
                PrintAllInfo.class,
                PrintUser.class,
                PrintUserOrgan.class,
                SetOrganStatus.class,
                Help.class,
                Save.class,
                Load.class
        })

public class BaseCommand implements Runnable {



    public void run() {
        System.out.println("Invalid command");
    }
}
