package seng302;

import java.util.Scanner;

public class App
{
    public static DonorManager donorManager;

    public static void main( String[] args )
    {
        String input;

        donorManager = new DonorManager();

        CommandParser commandParser = new CommandParser();

        Scanner sc = new Scanner(System.in);

        while (!(input = sc.nextLine()).equals("exit")) {
            commandParser.parseCommand(input);
        }
    }

    public static DonorManager getManager() {
        return donorManager;
    }
}
