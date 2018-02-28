package seng302;

import java.util.Scanner;

public class App
{
    public static DataStorage dataStorage;

    public static void main( String[] args )
    {
        String input;

        dataStorage = new DataStorage();

        CommandParser commandParser = new CommandParser();

        Scanner sc = new Scanner(System.in);

        while (!(input = sc.nextLine()).equals("exit")) {
            commandParser.parseCommand(input);
        }
    }
}
