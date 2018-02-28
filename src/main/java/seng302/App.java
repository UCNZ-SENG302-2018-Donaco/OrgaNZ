package seng302;

import seng302.commands.CommandHandler;

import java.util.Scanner;

public class App
{
    private static DonorManager donorManager;

    public static void main( String[] args )
    {
        String input;

        donorManager = new DonorManager();
        CommandHandler commandHandler = new CommandHandler(donorManager);

        Scanner sc = new Scanner(System.in);

        while (!(input = sc.nextLine()).equals("exit")) {
            commandHandler.parseCommand(input);
        }
    }

    public static DonorManager getManager() {
        return donorManager;
    }
}
